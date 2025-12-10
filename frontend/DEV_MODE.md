# 🚀 Modo Desarrollo Local

Esta guía te permite trabajar con el frontend en modo desarrollo (`npm run dev`) sin necesidad de reconstruir Docker cada vez que hagas cambios.

## 📋 Requisitos

- Node.js 20+ instalado localmente
- Backend corriendo en Docker

## ⚡ Inicio Rápido

### 1. Levantar solo el backend en Docker
```bash
# Desde la raíz del proyecto
docker compose up db risk-central credit-service -d
```

Esto levantará:
- PostgreSQL en puerto **5432**
- Backend en puerto **8080**
- Mock de servicio de riesgo en puerto **8081**

### 2. Instalar dependencias del frontend (solo la primera vez)
```bash
cd frontend
npm install
```

### 3. Iniciar el servidor de desarrollo
```bash
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

## 🔥 Ventajas del Modo Desarrollo

- ✅ **Hot Module Replacement (HMR)**: Los cambios se reflejan instantáneamente
- ✅ **Sin rebuilds de Docker**: Solo guardas y ves los cambios
- ✅ **Developer Tools**: Mejor experiencia con React DevTools
- ✅ **Source Maps**: Debugging más fácil
- ✅ **Velocidad**: Cambios en milisegundos vs minutos

## 📝 Workflow de Desarrollo

### Hacer cambios en el frontend
1. Edita cualquier archivo en `frontend/src/`
2. Guarda el archivo
3. El navegador se actualiza automáticamente
4. ¡Listo! 🎉

### Hacer cambios en el backend
1. Edita archivos Java en `credit-application-service/src/`
2. Reconstruye solo el backend:
   ```bash
   docker compose up --build credit-service -d
   ```
3. El frontend sigue corriendo sin interrupciones

## 🌐 URLs en Desarrollo

| Servicio | URL |
|----------|-----|
| **Frontend Dev** | http://localhost:5173 |
| **Backend API** | http://localhost:8080/api |
| **Swagger UI** | http://localhost:8080/api/swagger-ui.html |
| **Health Check** | http://localhost:8080/api/actuator/health |
| **PostgreSQL** | localhost:5432 |

## 🔧 Configuración

El archivo `.env` en la carpeta `frontend/` configura la URL del backend:

```env
VITE_API_URL=http://localhost:8080/api
```

Esta variable se usa en `src/services/api.ts` para todas las peticiones HTTP.

## 🐛 Solución de Problemas

### Error: CORS policy blocking
✅ **Ya está configurado**: El backend acepta peticiones desde `http://localhost:5173`

### Error: Cannot connect to backend
```bash
# Verificar que el backend esté corriendo
docker ps

# Ver logs del backend
docker logs credit-application-service

# Verificar health
curl http://localhost:8080/api/actuator/health
```

### Error: npm dependencies not found
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### Puerto 5173 ya en uso
```bash
# Matar el proceso que usa el puerto
lsof -ti:5173 | xargs kill -9

# O usar otro puerto
npm run dev -- --port 5174
```

## 🎨 Scripts Disponibles

```bash
# Desarrollo con HMR
npm run dev

# Build de producción
npm run build

# Preview del build
npm run preview

# Linter
npm run lint

# Formatear código
npm run format
```

## 📦 Cuando usar Docker vs npm run dev

### Usa `npm run dev` para:
- ✅ Desarrollo activo del frontend
- ✅ Probar cambios rápidamente
- ✅ Debugging con React DevTools
- ✅ Experimentar con UI/UX

### Usa Docker para:
- ✅ Probar el build de producción
- ✅ Verificar nginx y proxy
- ✅ Testing end-to-end
- ✅ Deployment final

## 🔄 Comandos Útiles

### Solo backend en Docker
```bash
# Levantar
docker compose up db risk-central credit-service -d

# Ver logs
docker logs -f credit-application-service

# Reiniciar backend
docker compose restart credit-service

# Detener todo
docker compose down
```

### Frontend en desarrollo
```bash
cd frontend

# Desarrollo
npm run dev

# Build local
npm run build

# Preview del build
npm run preview
```

## 💡 Tips de Productividad

### 1. Abrir múltiples terminales
- **Terminal 1**: `npm run dev` (frontend)
- **Terminal 2**: `docker logs -f credit-application-service` (backend logs)
- **Terminal 3**: Para comandos git, docker, etc.

### 2. Extensiones de VSCode recomendadas
- ES7+ React/Redux/React-Native snippets
- Tailwind CSS IntelliSense
- ESLint
- Prettier
- Auto Import

### 3. Hot Keys útiles
- `Ctrl + C` en terminal: Detener servidor dev
- `Ctrl + Shift + R`: Hard refresh del navegador
- `F12`: Abrir DevTools

## 🎯 Flujo Recomendado

```bash
# 1. Primera vez del día
docker compose up db risk-central credit-service -d
cd frontend
npm run dev

# 2. Trabajar en frontend todo el día
# ... hacer cambios, guardar, ver resultados instantáneamente ...

# 3. Al terminar
# Ctrl + C para detener npm run dev
docker compose down
```

## 📚 Recursos

- [Vite Documentation](https://vitejs.dev/)
- [React Documentation](https://react.dev/)
- [TailwindCSS Documentation](https://tailwindcss.com/)
- [React Query Documentation](https://tanstack.com/query/latest)

---

¡Ahora puedes desarrollar con velocidad y sin esperas! 🚀✨
