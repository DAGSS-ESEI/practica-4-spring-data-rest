package es.uvigo.dagss.pedidos.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.dagss.pedidos.daos.AlmacenDao;
import es.uvigo.dagss.pedidos.daos.ArticuloAlmacenDao;
import es.uvigo.dagss.pedidos.entidades.Almacen;
import es.uvigo.dagss.pedidos.entidades.Articulo;
import es.uvigo.dagss.pedidos.entidades.ArticuloAlmacen;
import es.uvigo.dagss.pedidos.entidades.ArticuloAlmacenId;

@Service
public class AlmacenServiceImpl implements AlmacenService {
	@Autowired
	private AlmacenDao almacenDao;

	@Autowired
	private ArticuloAlmacenDao articuloAlmacenDao;

	@Override
	@Transactional
	public Almacen crear(Almacen almacen) {
		return almacenDao.save(almacen);
	}

	@Override
	@Transactional
	public Almacen modificar(Almacen almacen) {
		return almacenDao.save(almacen);
	}

	@Override
	@Transactional
	public void eliminar(Almacen almacen) {
		almacenDao.delete(almacen);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Almacen> buscarPorId(Long id) {
		return almacenDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Almacen> buscarTodos() {
		return almacenDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Almacen> buscarPorNombre(String patron) {
		return almacenDao.findByNombreContaining(patron);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Almacen> buscarPorLocalidad(String localidad) {
		return almacenDao.findByDireccionLocalidad(localidad);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Almacen> buscarPorArticuloId(Long articuloId) {
		return almacenDao.findByArticuloId(articuloId);
	}

	@Override
	public ArticuloAlmacen crearArticuloAlmacen(ArticuloAlmacen articuloAlmacen) {
		return articuloAlmacenDao.save(articuloAlmacen);
	}

	@Override
	@Transactional
	public ArticuloAlmacen crearArticuloAlmacen(Articulo articulo, Almacen almacen, Integer stock) {
		return this.crearArticuloAlmacen(new ArticuloAlmacen(articulo, almacen, stock));
	}

	@Override
	@Transactional
	public void eliminarArticuloAlmacen(ArticuloAlmacen articuloAlmacen) {
		articuloAlmacenDao.delete(articuloAlmacen);
	}

	@Override
	public void eliminarArticuloAlmacen(Long articuloId, Long almacenId) {
		Optional<ArticuloAlmacen> optional = articuloAlmacenDao.findById(new ArticuloAlmacenId(articuloId, almacenId));
		if (optional.isPresent()) {
			ArticuloAlmacen aa = optional.get();
			this.eliminarArticuloAlmacen(aa);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ArticuloAlmacen> buscarArticuloAlmacenPorArticuloIdAlmacenId(Long articuloId, Long almacenId) {
		return articuloAlmacenDao.findById(new ArticuloAlmacenId(articuloId, almacenId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ArticuloAlmacen> buscarArticulosAlmacenPorAlmacenId(Long almacenId) {
		return articuloAlmacenDao.findByAlmacenId(almacenId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ArticuloAlmacen> buscarArticulosAlmacenPorArticuloId(Long articuloId) {
		return articuloAlmacenDao.findByArticuloId(articuloId);
	}

	@Override
	public ArticuloAlmacen modificarArticuloAlmacen(ArticuloAlmacen articuloAlmacen) {
		return articuloAlmacenDao.save(articuloAlmacen);
	}

}
