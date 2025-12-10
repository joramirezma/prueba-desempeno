# Credenciales de Prueba

## Usuarios del Sistema

Todos los usuarios de prueba tienen la contraseña: **`password`**

### Administrador
- **Usuario**: `admin`
- **Contraseña**: `password`
- **Email**: admin@coopcredit.com
- **Rol**: ROLE_ADMIN
- **Permisos**: Acceso completo al sistema

### Analista
- **Usuario**: `analyst`
- **Contraseña**: `password`
- **Email**: analyst@coopcredit.com
- **Rol**: ROLE_ANALYST
- **Permisos**: Evaluar solicitudes de crédito pendientes

### Afiliado
- **Usuario**: `affiliate1`
- **Contraseña**: `password`
- **Email**: affiliate1@email.com
- **Documento**: 1017654321
- **Rol**: ROLE_AFFILIATE
- **Permisos**: Crear y consultar sus propias solicitudes de crédito

## Afiliados Registrados

1. **Juan Carlos Pérez**
   - Documento: 1017654321
   - Salario: $5,000,000
   - Estado: ACTIVO

2. **María García López**
   - Documento: 1017654322
   - Salario: $7,500,000
   - Estado: ACTIVO

3. **Carlos Rodríguez**
   - Documento: 1017654323
   - Salario: $3,500,000
   - Estado: ACTIVO

4. **Ana Martínez**
   - Documento: 1017654324
   - Salario: $6,000,000
   - Estado: INACTIVO

## Acceso al Sistema

- **URL Frontend**: http://localhost:3000
- **URL Backend**: http://localhost:8080/api
- **URL Swagger**: http://localhost:8080/api/swagger-ui.html

## Endpoints Principales

### Autenticación
- POST `/api/auth/login` - Iniciar sesión
- POST `/api/auth/register` - Registrar nuevo usuario

### Afiliados (requiere autenticación)
- GET `/api/affiliates` - Listar afiliados (ADMIN)
- POST `/api/affiliates` - Crear afiliado (ADMIN)
- GET `/api/affiliates/{id}` - Obtener afiliado (ADMIN)
- PUT `/api/affiliates/{id}` - Actualizar afiliado (ADMIN)
- GET `/api/affiliates/document/{documentNumber}` - Buscar por documento (AFFILIATE/ADMIN)

### Solicitudes de Crédito (requiere autenticación)
- GET `/api/applications` - Listar todas (ADMIN)
- GET `/api/applications/pending` - Listar pendientes (ANALYST/ADMIN)
- GET `/api/applications/{id}` - Obtener solicitud (Todos)
- POST `/api/applications` - Crear solicitud (Todos)
- PUT `/api/applications/{id}/evaluate` - Evaluar solicitud (ANALYST/ADMIN)
- GET `/api/applications/affiliate/{documentNumber}` - Solicitudes de afiliado (AFFILIATE/ADMIN)
