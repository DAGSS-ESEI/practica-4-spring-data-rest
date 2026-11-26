# Envío de peticiones HTTP

Todas las rutas del API están bajo la base `/api`. Las antiguas rutas de Spring Data REST (`/articuloes`, `/pedidoes`, ...) **no** están disponibles en este proyecto.

## `requests.http` + VS Code REST Client (recomendado)

1. Instala la extensión **REST Client** (`humao.rest-client`) en VS Code:
   - Desde VS Code: panel **Extensions** (`Ctrl+Shift+X`), busca **REST Client** (autor Huachao Mao) y pulsa **Install**.
   - Desde la terminal:
     ```bash
     code --install-extension humao.rest-client
     ```
   - Marketplace: https://marketplace.visualstudio.com/items?itemName=humao.rest-client
2. Abre [`../requests.http`](../requests.http).
3. Pulsa **Send Request** sobre la petición que quieras lanzar.

Este método tiene la ventaja de que las peticiones quedan en texto plano, y por tanto son versionables en Git, y además no requiere de cuenta ni de herramientas externas.

![REST Client (VS Code)](vscode.png "REST Client (VS Code)")

## `curl`

Ejemplos `GET`:
```shell
curl -v http://localhost:8080/api/articulos
curl -v http://localhost:8080/api/articulos/1
curl -v http://localhost:8080/api/clientes
curl -v http://localhost:8080/api/clientes/11111111A
curl -v http://localhost:8080/api/pedidos
curl -v http://localhost:8080/api/pedidos/1
curl -v http://localhost:8080/api/familias
curl -v http://localhost:8080/api/almacenes
```

Filtros:
```shell
curl -v "http://localhost:8080/api/articulos?familiaId=1"
curl -v "http://localhost:8080/api/articulos?descripcion=tubo"
curl -v "http://localhost:8080/api/clientes?nombre=Pepe"
curl -v "http://localhost:8080/api/clientes?localidad=Ourense"
curl -v "http://localhost:8080/api/pedidos?clienteDNI=11111111A"
curl -v "http://localhost:8080/api/almacenes?localidad=Santiago"
curl -v "http://localhost:8080/api/almacenes?articuloId=1"
```

Recursos anidados:
```shell
curl -v http://localhost:8080/api/almacenes/1/articulos
curl -v http://localhost:8080/api/almacenes/1/articulos/1/stock
```

Ejemplo `POST` (artículo):
```shell
curl -v --header 'Content-Type: application/json' \
  --data '{"nombre":"tubo POST","descripcion":"enviado por POST","precioUnitario":123.456,"familia":{"id":1}}' \
  --request POST http://localhost:8080/api/articulos
```

Ejemplo `POST` (cliente):
```shell
curl -v --header 'Content-Type: application/json' \
  --data '{"DNI":"33333333C","nombre":"Cliente POST","direccion":{"domicilio":"calle 9","localidad":"Vigo","codigoPostal":"36200","provincia":"Pontevedra","telefono":"986000000"}}' \
  --request POST http://localhost:8080/api/clientes
```

## Alternativas

- **Bruno**: cliente de escritorio offline y Git-native; las colecciones se guardan como ficheros en el repositorio.
- **Hoppscotch**: cliente en navegador, sin instalación; también se puede self-hostear.
- **Postman**: si ya lo usas; ten en cuenta que el plan gratuito es de un solo usuario desde marzo de 2026.
