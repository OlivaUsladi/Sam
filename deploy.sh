#!/bin/bash
# SAM Application — Deployment Script
# Run this on a fresh VPS (Ubuntu 22.04+)

set -e

echo "=== SAM App Deployment ==="
echo ""

# 1. Install Docker & Docker Compose
if ! command -v docker &> /dev/null; then
    echo "[1/5] Installing Docker..."
    curl -fsSL https://get.docker.com | sh
    sudo usermod -aG docker $USER
    echo "Docker installed. Please re-login and run this script again."
    exit 0
fi

# 2. Check .env file
if [ ! -f .env ]; then
    echo "ERROR: .env file not found!"
    echo "Copy .env.example to .env and fill in your values:"
    echo "  cp .env.example .env"
    echo "  nano .env"
    exit 1
fi

echo "[1/5] Docker OK"

# 3. Get SSL certificates (first time only)
DOMAIN="sam-app.ru"
if [ ! -d "/etc/letsencrypt/live/$DOMAIN" ]; then
    echo "[2/5] Getting SSL certificates..."
    
    # Start nginx temporarily for ACME challenge
    docker compose up -d nginx
    sleep 2
    
    docker compose run --rm certbot certonly \
        --webroot \
        --webroot-path=/var/www/certbot \
        -d $DOMAIN \
        -d api.$DOMAIN \
        --email support@$DOMAIN \
        --agree-tos \
        --no-eff-email
    
    docker compose down
    echo "SSL certificates obtained!"
else
    echo "[2/5] SSL certificates already exist"
fi

# 4. Build and start services
echo "[3/5] Building backend..."
docker compose build backend

echo "[4/5] Starting all services..."
docker compose up -d

echo "[5/5] Waiting for services to be healthy..."
sleep 10

# 5. Health check
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo ""
    echo "=== DEPLOYMENT SUCCESSFUL ==="
    echo "  API: https://api.$DOMAIN"
    echo "  Web: https://$DOMAIN"
    echo ""
else
    echo ""
    echo "WARNING: Health check failed. Check logs:"
    echo "  docker compose logs backend"
fi
