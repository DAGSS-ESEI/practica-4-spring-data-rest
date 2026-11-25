# Ejemplo Proyecto Spring 
> DAGSS 2026-2027, semana 4

- Ejemplo de endpoints REST con Spring MVC (implementación directa, sin HATEOAS)
- Construido sobre proyecto [practica-3-spring-data-jpa](https://github.com/DAGSS-ESEI/practica-3-spring-data-jpa)

## Previo
### Requisitos previos

- Servidor de BD MySQL 8.0.46 (o compatible)
- Java 25 (OpenJDK 25 u Oracle JDK 25)
- Maven 3.9.8
- (opcional) GIT
- (opcional) IDE Java (VSCode, Eclipse, Netbeans, IntelliJ)
- (recomendable) extensión *Spring Boot Extension Pack* en VSCode.

**Nota:** En los equipos de laboratorio puede ser conveniente establecer la variable de entorno `JAVA_HOME`, para que `mvn` compile y ejecute siempre con el mismo JDK.

```sh 
export JAVA_HOME=/usr/lib/jvm/jdk-25

export PATH=$JAVA_HOME/bin:$PATH 
```

*Ajustad la ruta a la instalación de Java 25 disponible en vuestro equipo.*

### Crear BD para los ejemplos  (si no se ha hecho antes)

* Crear BD "pruebas_dagss" en MySQL 

```
mysql -u root -p    [pedirá la contraseña de MySQL]

mysql> create database pruebas_dagss;
mysql> create user dagss@localhost identified by "dagss";
mysql> grant all privileges on pruebas_dagss.* to dagss@localhost;

```

Adicionalmente, puede ser necesario establecer un formato de fecha compatible
```
mysql> set @@global.time_zone = '+00:00';
mysql> set @@session.time_zone = '+00:00';
```

#### Con servidor de BD en contenedor Docker

Para no instalar MySQL localmente puede arrancarse un contenedor Docker (los datos se guardarán en un volumen gestionado por Docker llamado `dagss_mysql_dbdata`):

```bash
docker run -d --rm --name dagss-mysql \
  -e MYSQL_DATABASE=pruebas_dagss \
  -e MYSQL_USER=dagss \
  -e MYSQL_PASSWORD=dagss \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -p 3307:3306 \
  -v dagss_mysql_dbdata:/var/lib/mysql \
  mysql:8.0.46
```

Y probar:

```bash
docker exec -it dagss-mysql mysql -udagss -pdagss --default-character-set=utf8mb4 pruebas_dagss -e "SHOW TABLES;"
```

En este caso, configurar en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/pruebas_dagss
```

### Configurar _Datasource_

Editar fichero `src/main/resources/application.properties` y establecer los valores de conexión a la BD:

```properties
# Docker (3307); para MySQL local usa jdbc:mysql://localhost:3306/pruebas_dagss
spring.datasource.url=jdbc:mysql://localhost:3307/pruebas_dagss
spring.datasource.username=dagss
spring.datasource.password=dagss
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
# detalles en https://www.baeldung.com/hibernate-field-naming-spring-boot
```

**Nota:** con `create-drop`, Hibernate crea el esquema al arrancar y lo elimina al parar la aplicación; los datos no se conservan entre ejecuciones.

## Estructura del proyecto
El proyecto resultante sigue una organización relativamente habitual en proyectos Spring. 
En este caso, al tratarse de una aplicación muy sencilla es más compleja de lo necesario y sufre de cierta "_sobreingeniería_".

* Paquete **entidades**: incluye las entidades del dominio
* Paquete **daos**: incluye la definición de los `interfaces` _xxxDao_ que heredan de `JpaRepository<E,K>` y aportan las operaciones CRUD (_create_, _read_, _update_, _delete_) sobre las entidades del dominio (se inyectarán _proxies_ con las implementaciones de estos interfaces cuando se requiera su inyección con `@Autowired`, que implícitamente estarían marcadas con `@Repository`)
* Paquete **servicios**: incluye los _servicios_ que encapsulan operaciones de la lógica de la aplicación
    - Las operaciones se definen en los `interfaces` _xxxService_ y se implementan en las clases _xxxServiceImpl_, marcadas con `@Service`
    - Los métodos de los _xxxServiceImpl_ delegan las operaciones en los _DAO_ necesarios/correspondientes, inyectados con `@Autowired` (no es imprescindible una correspondencia directa con las entidades presentes en el dominio, ver como ejemplo el caso de `ArticuloServiceImpl`)
    - Las operaciones de _xxxService_ cumplen dos funciones complementarias:
       1. Ocultan y coordinan la operación de otros objetos _Service_ (`@Service`) o _DAO_ (`@Repository`), siguiendo un [patrón _Facade_ (Fachada)](https://es.wikipedia.org/wiki/Facade_(patrón_de_diseño)) que oculta a la capa superior la interacción entre esos objetos
       
       2. Algunos de sus métodos están marcados con `@Transactional` delimitando operaciones trasaccionales que serán invocadas desde la capa superior. 
       
          Se asegura así que las modificaciones realizadas en estos métodos (y en los métodos de otros componentes invocados por estos) se ejecutan todas o ninguna, manteniendo las [propiedades ACID](https://es.wikipedia.org/wiki/ACID) 
* Paquete **controladores**: incluye las clases marcadas con `@RestController` que aportan los métodos que son mapeados a las peticiones HTTP que conforman el API REST de la aplicación
    - Cada _xxxController_ está anotado con `@RestController` y delega en uno o más _yyyService_ (inyectados con `@Autowired`) la implementación de la lógica que corresponda (no es imprescindible una correspondencia directa con las entidades presentes en el dominio)
    - Todas la URIs del API REST se ubican bajo la URI base `api`
    - Los controladores están anotados con `@CrossOrigin(origins = "*")` para permitir accesos al API REST desde aplicaciones Javascript (ver https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)
    - La estructura del API REST definido sigue la estructura típica de las **aplicaciones _RESTFul_**
      - Se define un _endpoint_ REST para cada Recurso (~ Entidad) relevante para la aplicación, la **URI base** de cada recurso se identifica con el **nombre del recurso/entidad en plural** (ver anotaciones `@RequestMapping` en cada Controller)
      - Las peticiones **HTTP GET** sobre la **URI base** de cada Recurso se usan para realizar **búsquedas** con diferentes parámetros, devolviendo una lista de entidades codificadas como un array JSON
      - Las peticiones **HTTP POST** sobre la **URI base** de cada Recurso se usan para **crear nuevos recursos/entidades**. Los atributos del recurso/entidad a crear se pasan en el cuerpo de la petición POST codificados como objetos JSON. La respuesta HTTP generada contiene la URI del nuevo recurso/entidad creado en el parámetro `Location` (fijarse en `crearURIArticulo`, por ejemplo).
      - Las peticiones **HTTP GET** sobre la URI base de cada Recurso seguida del **fragmento de path** `/{id}`  (mapeado como argumento del método con `@PathVariable`) se usan para **acceder** al recurso/entidad identificado **por la clave primaria** indicada. El recurso/entidad se devuelve como un objeto JSON en el cuerpo de la respuesta HTTP.
      - Las peticiones **HTTP DELETE** sobre la URI base de cada Recurso seguida del **fragmento de path** `/{id}`  (mapeado como argumento del método con `@PathVariable`) se usan para **eliminar** el recurso/entidad identificado **por la clave primaria** indicada.
      - Las peticiones **HTTP PUT** sobre la URI base de cada Recurso seguida del **fragmento de path** `/{id}`  (mapeado como argumento del método con `@PathVariable`) se usan para **modificar** los atributos del recurso/entidad identificado **por la clave primaria** indicada. Los nuevos valores de los atributos del recurso/entidad se envían como un objeto JSON en el cuerpo de la petición HTTP PUT.
      - En los casos donde sea relevante, las **relaciones de un recurso** dado con otros recursos/entidades se mapean en **URIs anidadas** bajo el fragmento de path `/{id}`, usando el nombre en plural del recurso/entidad  al otro lado de la relación (por ejemplo `/api/almacenes/1/articulos`). Sobre esos recursos anidados pueden aplicarse peticiones GET (listado de recursos relacionados), POST (establecimiento de la relación con un nuevo recurso relacionado), DELETE sobre un `/{idRelacionado}` (eliminación de la relación con el recurso relacionado identificado por su clave primaria) o GET sobre un `/{idRelacionado}` (acceso al recurso relacionado identificado por su clave primaria).
* Paquete **controladores.excepciones**: ejemplo de gestión de excepciones en Spring MVC
   - Declara 2 excepciones propias de la aplicación (`ResourceNotFoundException` y `WrongParameterException`) que heredan de `RuntimeException`
   - Declara un _manejador de excepciones_ global, `GlobalExceptionHandler`, anotado con `@RestControllerAdvice` (anotación derivada de [`@ControllerAdvice`](https://www.baeldung.com/exception-handling-for-rest-with-spring#controlleradvice))
      - Genera las respuestas HTTP para peticiones que provoquen excepciones 
      - Captura y maneja `ResourceNotFoundException`, `WrongParameterException`,  `MethodArgumentNotValidException` [fallos de [Bean Validation](https://www.baeldung.com/spring-boot-bean-validation) en los parámetros anotados con `@Valid`] y `Exception` [respuesta genérica ante excepciones]
      - Cada respuesta a excepciones tiene un código `HttpStatus` de respuesta propio e incorpora en el cuerpo de la respuesta datos adicionales encapsulando un objeto [ProblemDetail](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)

## Añadidos

Con respecto al proyecto anterior, se ha añadido:

* En `pom.xml`:
    * Añadida dependencia con _starter_ para Spring MVC (`spring-boot-starter-web`) 
    * Añadida dependencia con _starter_ para Spring Validation (`spring-boot-starter-validation`) [Implementación Spring de la especificación [Bean Validation](https://www.baeldung.com/spring-boot-bean-validation), permite usar la anotación `@Valid` para validar los atributos de los objetos JSON recibidos en las peticiones HTTP del API REST] 
* En paquete `entidades`: 
	* Añadida anotación `@JsonIgnore` en relación `@ManyToOne` de `LineaPedido` hacia `Pedido` para evitar ciclos en la serialización JSON de los pedidos.
* Creado paquete `services` con:
	* `ClienteService` y `ClienteServiceImpl.java` 	
	* `ArticuloService` y `ArticuloServiceImpl.java` (gestiona Articulos y Familias)	
	* `AlmacenService` y `AlmacenServiceImpl.java`  (gestiona Almacenes y ArticuloAlmacen [=stocks])
	* `PedidoService` y `PedidoServiceImpl`	
* Creado paquete `controllers` con:
   * `ClienteController`: API REST para cliente (en `http://localhost:8080/api/clientes`)
   * `ArticuloController`: API REST para artículos (en `http://localhost:8080/api/articulos`)
   * `FamiliaController`: API REST para familias (en `http://localhost:8080/api/familias`)
   * `PedidoController`: API REST para pedidos (en `http://localhost:8080/api/pedidos`)
   * `AlmacenController`: API REST para almacenes y stock de artículos en almacén (en `http://localhost:8080/api/almacenes`) 
   

## Ejecución del proyecto y pruebas:

Ejecuta el proyecto:
- En Spring Tool Suite: Proyecto 'pedidos-spring' `[botón derecho] > Run As > Spring Boot App`.
- Desde línea de comandos:
```sh
mvn spring-boot:run
```

**Notas**: 

* El .jar resultante ejecuta su propio contenedor de Servlet Apache Tomcat embebido, desplegando la aplicación en http://localhost:8080/api
* Se puede acceder desde el navegador o mediante  `curl -v` a las URIs del API:
   - http://localhost:8080/api/clientes
   - http://localhost:8080/api/clientes/11111111A
   - http://localhost:8080/api/articulos/3
   - http://localhost:8080/api/almacenes/1/articulos
   - http://localhost:8080/api/almacenes/1/articulos/2/stock

**Pruebas**: 
* Realiza pruebas enviando peticiones HTTP con `curl` o con extensiones que faciliten este trabajo. Más detalles [aquí](doc/peticiones_REST.md).

### EXTRA: SpringDoc OpenAPI (generador de documentación on-line OpenAPI/Swagger) [no es parte de Spring]

1. Descomentar en el `pom.xml` las  dependencias que activan el proyecto `springdoc-openapi`
   - Detalles en https://springdoc.org/

2. Ejecutar de nuevo el proyecto (con `mvn spring-boot:run`) y acceder con un navegador a la URL http://localhost:8080/swagger-ui.html para ver la documentación autogenerada para el API REST.


