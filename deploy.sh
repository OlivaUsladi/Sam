#!/bin/bash
# SAM Application — Deployment Script
# Run this on the VPS from the repository root (the folder with docker-compose.yml).
# Safe to run repeatedly: it only requests SSL certificates when they are missing.

set -e
cd "$(dirname "$0")"

DOMAIN="sam-app.ru"
SSL_CONF="nginx/conf.d/ssl.conf"

echo "=== SAM App Deployment ==="
echo ""

# 1. Docker
if ! command -v docker &> /dev/null; then
    echo "[1/6] Installing Docker..."
    curl -fsSL https://get.docker.com | sh
    sudo usermod -aG docker "$USER"
    echo "Docker installed. Please log out, log in again and run this script again."
    exit 0
fi
echo "[1/6] Docker OK"

# 2. .env
if [ ! -f .env ]; then
    echo "ERROR: .env file not found!"
    echo "Create it first:  ./init-secrets.sh   (then fill in MAIL_PASSWORD with nano .env)"
    exit 1
fi
echo "[2/6] .env OK"

# 3. Landing files
if [ ! -f landing/index.html ]; then
    echo "ERROR: landing/index.html not found — the website files are missing."
    exit 1
fi
echo "[3/6] landing/ OK"

# 4. SSL certificates (stored inside the docker volume 'certbot_data', not on the host)
has_cert() {
    docker compose run --rm --entrypoint sh certbot -c "test -f /etc/letsencrypt/live/$DOMAIN/fullchain.pem" 2>/dev/null
}

if has_cert; then
    echo "[4/6] SSL certificates already exist"
    [ -f "$SSL_CONF.off" ] && mv "$SSL_CONF.off" "$SSL_CONF"
else
    echo "[4/6] Getting SSL certificates (HTTP-01 challenge on port 80)..."

    # nginx must start without the 443 config, otherwise it fails on missing cert files
    [ -f "$SSL_CONF" ] && mv "$SSL_CONF" "$SSL_CONF.off"
    docker compose up -d nginx
    sleep 3

    docker compose run --rm certbot certonly \
        --webroot \
        --webroot-path=/var/www/certbot \
        -d "$DOMAIN" \
        -d "api.$DOMAIN" \
        --email "support@$DOMAIN" \
        --agree-tos \
        --no-eff-email

    mv "$SSL_CONF.off" "$SSL_CONF"
    echo "SSL certificates obtained!"
fi

# 5. Build & start
echo "[5/6] Building backend (first time may take 5-15 minutes)..."
docker compose build backend

echo "[6/6] Starting all services..."
docker compose up -d
docker compose exec nginx nginx -s reload || true

echo "Waiting for backend to start..."
for i in $(seq 1 24); do
    if curl -s http://localhost:8080/actuator/health | grep -q '"UP"'; then
        echo ""
        echo "=== DEPLOYMENT SUCCESSFUL ==="
        echo "  Web: https://$DOMAIN"
        echo "  API: https://api.$DOMAIN/actuator/health"
        echo ""
        exit 0
    fi
    sleep 5
done

echo ""
echo "WARNING: backend health check did not return UP within 2 minutes."
echo "Check logs:  docker compose logs --tail=100 backend"
exit 1
