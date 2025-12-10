# CoopCredit Frontend

Frontend para el Sistema de Solicitudes de Crédito de CoopCredit, desarrollado con React, TypeScript y TailwindCSS.

## 🚀 Tecnologías

- **React 18** - Biblioteca UI
- **TypeScript** - Tipado estático
- **Vite** - Build tool y dev server
- **TailwindCSS** - Framework de estilos
- **React Router** - Enrutamiento SPA
- **React Query** - Gestión de estado del servidor
- **React Hook Form** - Manejo de formularios
- **Axios** - Cliente HTTP

## 📋 Requisitos

- Node.js 18+ 
- npm o yarn
- Backend `credit-application-service` corriendo en puerto 8080

## 🛠️ Instalación

```bash
# Instalar dependencias
npm install

# Iniciar en modo desarrollo
npm run dev

# Construir para producción
npm run build

# Vista previa de producción
npm run preview
```

## 🌐 Configuración

El frontend usa un proxy en desarrollo para redirigir las peticiones `/api/*` al backend en `localhost:8080`.

Para producción, configura la variable de entorno:
```
VITE_API_URL=http://tu-backend:8080/api
```

## 👥 Usuarios de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | ADMIN |
| `analyst` | `analyst123` | ANALYST |
| `affiliate1` | `affiliate123` | AFFILIATE |

## 📱 Funcionalidades por Rol

### ADMIN
- ✅ Dashboard completo
- ✅ Gestión de afiliados (CRUD)
- ✅ Ver todas las solicitudes
- ✅ Evaluar solicitudes pendientes
- ✅ Crear solicitudes para afiliados

### ANALYST
- ✅ Dashboard con accesos rápidos
- ✅ Ver lista de afiliados
- ✅ Ver solicitudes pendientes
- ✅ Evaluar solicitudes

### AFFILIATE
- ✅ Dashboard personalizado
- ✅ Crear nuevas solicitudes de crédito
- ✅ Ver mis solicitudes y su estado

## 🐳 Docker

```bash
# Construir imagen
docker build -t coopcredit-frontend .

# Ejecutar contenedor
docker run -p 3000:80 coopcredit-frontend
```

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── components/     # Componentes reutilizables
│   │   ├── ui/         # Componentes de UI (Button, Input, etc.)
│   │   ├── layout/     # Layout y Navbar
│   │   └── auth/       # ProtectedRoute
│   ├── context/        # Contextos de React (AuthContext)
│   ├── pages/          # Páginas de la aplicación
│   │   ├── auth/       # Login y Register
│   │   ├── dashboard/  # Dashboard principal
│   │   ├── affiliates/ # Gestión de afiliados
│   │   └── applications/ # Gestión de solicitudes
│   ├── services/       # Servicios de API
│   ├── types/          # Tipos TypeScript
│   ├── App.tsx         # Componente raíz con rutas
│   ├── main.tsx        # Punto de entrada
│   └── index.css       # Estilos globales
├── public/             # Archivos estáticos
├── Dockerfile          # Configuración Docker
├── nginx.conf          # Configuración Nginx para producción
└── package.json        # Dependencias y scripts
```

## 🔗 Endpoints del Backend

El frontend consume los siguientes endpoints:

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario

### Afiliados
- `GET /api/affiliates` - Listar afiliados
- `GET /api/affiliates/{doc}` - Obtener afiliado
- `POST /api/affiliates` - Crear afiliado
- `PUT /api/affiliates/{doc}` - Actualizar afiliado
- `POST /api/affiliates/{doc}/activate` - Activar
- `POST /api/affiliates/{doc}/deactivate` - Desactivar

### Solicitudes
- `GET /api/applications` - Listar todas (ADMIN)
- `GET /api/applications/pending` - Listar pendientes
- `GET /api/applications/{id}` - Obtener por ID
- `GET /api/applications/affiliate/{doc}` - Por afiliado
- `POST /api/applications` - Crear solicitud
- `POST /api/applications/{id}/evaluate` - Evaluar

## 📝 Licencia

MIT
