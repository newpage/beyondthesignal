FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG VITE_BATTLE_TELEMETRY_WS_URL=ws://localhost:8080/ws/battle-telemetry
ARG VITE_GAME_API_URL=http://localhost:8080
ARG VITE_USE_DEMO_TELEMETRY=false
ENV VITE_BATTLE_TELEMETRY_WS_URL=$VITE_BATTLE_TELEMETRY_WS_URL
ENV VITE_GAME_API_URL=$VITE_GAME_API_URL
ENV VITE_USE_DEMO_TELEMETRY=$VITE_USE_DEMO_TELEMETRY
RUN npm run build

FROM nginx:1.29-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
