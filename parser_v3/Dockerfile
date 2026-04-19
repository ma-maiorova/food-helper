FROM python:3.12-slim

# --- системные зависимости для Chrome + Xvfb + cron ---
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    ca-certificates \
    fonts-liberation \
    xvfb \
    cron \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxi6 \
    libxrandr2 \
    libxrender1 \
    libxss1 \
    libxtst6 \
    libxkbcommon0 \
    libcairo2 \
    libgbm1 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libgdk-pixbuf-xlib-2.0-0 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libnspr4 \
    libnss3 \
    libasound2t64 \
    libglib2.0-0 \
    libatspi2.0-0 \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# --- Chrome + ChromeDriver (версия 145, архитектура определяется автоматически) ---
ENV CHROME_VERSION=145.0.7632.117
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then CHROME_ARCH="linux-arm64"; else CHROME_ARCH="linux64"; fi && \
    echo "Downloading Chrome for $CHROME_ARCH" && \
    wget -q "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/${CHROME_ARCH}/chrome-${CHROME_ARCH}.zip" \
    && unzip -q chrome-${CHROME_ARCH}.zip \
    && mv chrome-${CHROME_ARCH} /opt/chrome \
    && ln -s /opt/chrome/chrome /usr/local/bin/google-chrome \
    && rm chrome-${CHROME_ARCH}.zip \
    && wget -q "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/${CHROME_ARCH}/chromedriver-${CHROME_ARCH}.zip" \
    && unzip -q chromedriver-${CHROME_ARCH}.zip \
    && mv chromedriver-${CHROME_ARCH}/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf chromedriver-${CHROME_ARCH}.zip chromedriver-${CHROME_ARCH}

# --- Python-зависимости ---
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# --- исходники ---
COPY . .

RUN mkdir -p /app/data

COPY entrypoint.sh /entrypoint.sh
COPY scheduler_entrypoint.sh /scheduler_entrypoint.sh
COPY run_pipeline.sh /app/run_pipeline.sh
RUN chmod +x /entrypoint.sh /scheduler_entrypoint.sh /app/run_pipeline.sh

ENTRYPOINT ["/entrypoint.sh"]