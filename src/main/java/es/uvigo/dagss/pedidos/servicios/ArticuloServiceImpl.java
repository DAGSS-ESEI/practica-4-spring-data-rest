package es.uvigo.dagss.pedidos.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uvigo.dagss.pedidos.daos.ArticuloDao;
import es.uvigo.dagss.pedidos.daos.FamiliaDao;
import es.uvigo.dagss.pedidos.entidades.Articulo;
import es.uvigo.dagss.pedidos.entidades.Familia;

@Service
public class ArticuloServiceImpl implements ArticuloService {
	@Autowired
	ArticuloDao articuloDao;

	@Autowired
	FamiliaDao familiaDao;

	@Override
	@Transactional
	public Articulo crear(Articulo articulo) {
		return articuloDao.save(articulo);
	}

	@Override
	@Transactional
	public Articulo modificar(Articulo articulo) {
		return articuloDao.save(articulo);
	}

	@Override
	@Transactional
	public void eliminar(Articulo articulo) {
		articuloDao.delete(articulo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Articulo> buscarPorId(Long id) {
		return articuloDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Articulo> buscarTodos() {
		return articuloDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Articulo> buscarPorDescripcion(String patron) {
		return articuloDao.findByPatronDescripcion(patron);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Articulo> buscarPorFamilia(Long idFamilia) {
		return articuloDao.findByFamiliaId(idFamilia);
	}

	@Override
	@Transactional
	public Familia crearFamilia(Familia familia) {
		return familiaDao.save(familia);
	}

	@Override
	@Transactional
	public Familia modificarFamilia(Familia familia) {
		return familiaDao.save(familia);
	}

	@Override
	@Transactional
	public void eliminarFamilia(Familia familia) {
		familiaDao.delete(familia);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Familia> buscarFamiliaPorId(Long id) {
		return familiaDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Familia> buscarFamilias() {
		return familiaDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Familia> buscarFamiliasPorDescripcion(String patron) {
		return familiaDao.findByPatronDescripcion(patron);
	}

}
