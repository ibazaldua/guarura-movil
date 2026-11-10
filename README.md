# Guarura Móvil (Free)

App Android de control de llamadas creada por Bazaldúa Bros Solutions entre 2011 y 2012. Proyecto archivado: se conserva como respaldo e historial.

## Qué hacía
- Estadísticas de llamadas y mensajes por periodo con día de corte.
- Alerta de minutos y corte automático de llamadas al llegar al límite.
- Opción de remarcar después del corte.
- Lista negra para bloquear números.
- Números frecuentes.
- Contactos de la SIM.
- Historial propio en SQLite, porque el servicio `CallMonitor` registraba las llamadas entrantes y salientes.
- Versión Free con anuncios de AdMob; también existía una versión de paga.

## Datos técnicos
- Paquete: `mx.bazaldua.guaruramovil.free`, versión 1.3 (`versionCode` 4).
- `minSdkVersion` 8 (Android 2.2) y `targetSdkVersion` 13. Se compila contra `android-15`.
- Proyecto de Eclipse ADT (`.project`, `.classpath`, `project.properties`) en Java.
- Dependencia: `GoogleAdMobAdsSdk-4.0.4.jar`. `.classpath` lo busca en una ruta local antigua (`C:/IVAN/Downloads/...`), y el jar no viene en este repo.
- Permisos: contactos, llamadas, estado del teléfono, llamadas salientes, vibración e internet.

## Qué no viene en este repo
Está a propósito fuera de git. El respaldo sigue en Dropbox (`Workspace\GuaruraMovilFree` y `Workspace\Proyecto Guarura Móvil`):
- APKs compilados;
- carpetas `bin/` y `gen/`;
- llaves de firma `PrivateKey*`;
- configuración de FTP;
- respaldos `.bak`;
- video promocional.

## Compilar hoy
Las herramientas de Ant y ADT ya no tienen soporte. Para compilarlo habría que importarlo en Android Studio ("Import project (Eclipse ADT)") y luego:
- cambiar AdMob por Google Mobile Ads;
- subir `targetSdkVersion`;
- ajustar los permisos de llamadas a las políticas actuales de Google Play.
