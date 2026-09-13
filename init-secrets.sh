#!/bin/bash
# Generate .env file with random secrets
# Run once on first setup: ./init-secrets.sh

if [ -f .env ]; then
    echo ".env already exists! Delete it first if you want to regenerate."
    exit 1
fi

# Backend expects base64 values: JWT/HMAC >= 32 bytes, AES exactly 32 bytes after decode
DB_PASSWORD=$(openssl rand -hex 24)
JWT_SECRET=$(openssl rand -base64 48)
EMAIL_HMAC_KEY=$(openssl rand -base64 32)
EMAIL_AES_KEY=$(openssl rand -base64 32)

cat > .env << EOF
# Database
DB_PASSWORD=${DB_PASSWORD}

# JWT
JWT_SECRET=${JWT_SECRET}

# Email encryption keys (base64; HMAC >= 32 bytes, AES exactly 32 bytes)
EMAIL_HMAC_KEY=${EMAIL_HMAC_KEY}
EMAIL_AES_KEY=${EMAIL_AES_KEY}

# SMTP (fill in after setting up Yandex 360)
MAIL_HOST=smtp.yandex.ru
MAIL_PORT=465
MAIL_USERNAME=noreply@sam-app.ru
MAIL_PASSWORD=FILL_IN_YANDEX_APP_PASSWORD

# Application
MAIL_FROM=noreply@sam-app.ru
MAIL_SUPPORT=support@sam-app.ru
FRONTEND_BASE_URL=https://sam-app.ru
EOF

echo "✓ .env created with generated secrets!"
echo ""
echo "IMPORTANT: You still need to fill in MAIL_PASSWORD"
echo "  (Yandex 360 app password for noreply@sam-app.ru)"
echo ""
echo "Run: nano .env"
