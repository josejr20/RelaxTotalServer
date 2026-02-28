# ===== Stage 1: Build =====
FROM node:20-alpine AS build

WORKDIR /app

# Copia dependencias primero (mejor caché)
COPY package*.json ./

RUN npm install

# Copia el resto del código
COPY . .

# Construye la app
RUN npm run build


# ===== Stage 2: Producción =====
FROM nginx:alpine

# Copia los archivos generados
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]