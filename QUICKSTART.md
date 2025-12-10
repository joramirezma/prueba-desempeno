# 🚀 Guía de Inicio Rápido

## ⚡ Inicio Rápido (3 minutos)

### 1. Levantar el sistema
```bash
docker compose up -d
```

### 2. Acceder al sistema
🌐 **Frontend**: http://localhost:3000

### 3. Iniciar sesión
Usuario: `affiliate1`  
Contraseña: `password`

## 👥 Usuarios Disponibles

| Usuario | Contraseña | Rol | Documento |
|---------|-----------|-----|-----------|
| `admin` | `password` | Administrador | - |
| `analyst` | `password` | Analista | - |
| `affiliate1` | `password` | Afiliado | 1017654321 |

## 📱 URLs del Sistema

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Swagger/OpenAPI**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health

## 🎯 Funcionalidades por Rol

### 🔴 ADMIN (admin/password)
- ✅ Gestionar afiliados (crear, editar, ver todos)
- ✅ Ver todas las solicitudes de crédito
- ✅ Evaluar solicitudes pendientes
- ✅ Acceso completo al sistema

### 🟡 ANALYST (analyst/password)
- ✅ Ver solicitudes pendientes
- ✅ Evaluar solicitudes (aprobar/rechazar)
- ✅ Agregar comentarios a evaluaciones

### 🟢 AFFILIATE (affiliate1/password)
- ✅ Crear nuevas solicitudes de crédito
- ✅ Ver sus propias solicitudes
- ✅ Consultar estado de solicitudes por documento

## 🔄 Flujo de Prueba Rápido

### Como Afiliado (affiliate1)
1. Ingresar con `affiliate1` / `password`
2. Ir a "Crear Solicitud"
3. Ingresar monto: `10000000` y plazo: `24` meses
4. Enviar solicitud

### Como Analista (analyst)
1. Cerrar sesión
2. Ingresar con `analyst` / `password`
3. Ir a "Solicitudes Pendientes"
4. Ver la solicitud creada con evaluación de riesgo
5. Aprobar o rechazar con comentarios

### Como Admin (admin)
1. Cerrar sesión
2. Ingresar con `admin` / `password`
3. Ir a "Afiliados" → ver/crear/editar afiliados
4. Ir a "Solicitudes" → ver todas las solicitudes
5. Ir a "Pendientes" → evaluar solicitudes

## 🛠️ Comandos Útiles

### Ver estado de contenedores
```bash
docker ps
```

### Ver logs en tiempo real
```bash
docker logs -f credit-application-service
docker logs -f coopcredit-frontend
```

### Reiniciar el sistema
```bash
docker compose restart
```

### Reconstruir todo
```bash
docker compose down
docker compose up --build -d
```

### Limpiar todo (incluyendo base de datos)
```bash
docker compose down -v
```

## 📊 Datos de Ejemplo Precargados

### Afiliados
1. **Juan Carlos Pérez**
   - Documento: `1017654321`
   - Salario: $5,000,000
   - Estado: ACTIVO

2. **María García López**
   - Documento: `1017654322`
   - Salario: $7,500,000
   - Estado: ACTIVO

3. **Carlos Rodríguez**
   - Documento: `1017654323`
   - Salario: $3,500,000
   - Estado: ACTIVO

4. **Ana Martínez**
   - Documento: `1017654324`
   - Salario: $6,000,000
   - Estado: INACTIVO

## 🐛 Solución de Problemas

### ❌ Error: Container unhealthy
```bash
# Verificar que todos los servicios estén levantados
docker compose up -d

# Esperar 30-60 segundos para que pasen los health checks
docker ps
```

### ❌ Error 403 al hacer login
✅ **Solución**: La contraseña correcta es `password` (no `admin123` ni `affiliate123`)

### ❌ Frontend no carga
```bash
# Reconstruir frontend
docker compose up --build frontend -d
```

### ❌ Backend no responde
```bash
# Ver logs del backend
docker logs credit-application-service

# Reiniciar backend
docker compose restart credit-service
```

## 📝 Probar API con curl

### Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"affiliate1","password":"password"}'
```

### Crear Solicitud (requiere token)
```bash
TOKEN="tu_token_jwt_aqui"

curl -X POST http://localhost:3000/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "affiliateDocumentNumber": "1017654321",
    "requestedAmount": 10000000,
    "termMonths": 24
  }'
```

## 📚 Más Información

- Ver documentación completa en `README.md`
- Ver credenciales detalladas en `frontend/CREDENTIALS.md`
- Importar colección Postman desde `postman/CoopCredit.postman_collection.json`

---

## ✅ Checklist de Verificación

- [ ] Contenedores levantados: `docker ps`
- [ ] Frontend accesible: http://localhost:3000
- [ ] Backend accesible: http://localhost:8080/api/actuator/health
- [ ] Login exitoso con `affiliate1` / `password`
- [ ] Dashboard carga correctamente
- [ ] Puede crear una solicitud
- [ ] Puede ver solicitudes propias

¡Listo! El sistema está funcionando correctamente. 🎉
