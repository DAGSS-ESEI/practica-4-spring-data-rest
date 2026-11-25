# Envío de peticiones HTTP

Todas las rutas del API están bajo la base `/api`. Las antiguas rutas de Spring Data REST (`/articuloes`, `/pedidoes`, ...) **no** están disponibles en este proyecto.

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

## Extensiones

Las siguientes extensiones permiten gestionar colecciones de peticiones y ejecutarlas desde las propias extensiones:

- VS Code: [Thunder Client](https://marketplace.visualstudio.com/items?itemName=rangav.vscode-thunder-client)
- Firefox: [RESTED](https://addons.mozilla.org/en-US/firefox/addon/rested)
- Chrome: [Talend API Tester](https://chromewebstore.google.com/detail/talend-api-tester-free-ed/aejoelaoggembcahagimdiliamlcdmfm?pli=1)

Por ejemplo, la petición `POST` anterior se vería de la siguiente manera en Thunder Client (VS Code).

![Thunder Client (VS Code)](vscode.png "Thunder Client (VS Code)")
