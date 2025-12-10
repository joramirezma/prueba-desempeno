#!/bin/bash

# Script para iniciar el entorno de desarrollo completo

echo "🚀 Iniciando CoopCredit Development Environment"
echo ""

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    echo "Por favor inicia Docker e intenta nuevamente"
    exit 1
fi

echo "📦 Iniciando servicios backend con Docker Compose..."
docker compose up -d

echo ""
echo "⏳ Esperando a que los servicios estén listos..."
sleep 5

echo ""
echo "🔍 Estado de los servicios:"
docker compose ps

echo ""
echo "✅ Servicios backend iniciados!"
echo ""
echo "📝 Servicios disponibles:"
echo "   - Base de Datos: localhost:5432"
echo "   - Backend API: http://localhost:8080"
echo "   - API Docs: http://localhost:8080/swagger-ui.html"
echo "   - Risk Service: http://localhost:8081"
echo ""
echo "💡 Para iniciar el frontend:"
echo "   cd frontend && npm run dev"
echo ""
echo "📌 Comandos útiles:"
echo "   Ver logs: docker compose logs -f"
echo "   Detener servicios: docker compose down"
