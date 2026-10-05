package com.senai.template.controllers;

import com.senai.template.dtos.CategoriaDto;
import com.senai.template.dtos.ProdutoDto;
import com.senai.template.dtos.UsuarioDto;
import com.senai.template.dtos.MovimentacaoDto;
import com.senai.template.entities.ProdutoEntity;
import com.senai.template.sessoes.SessaoUtil;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.senai.template.services.CategoriaService;
import com.senai.template.services.ProdutoService;
import com.senai.template.services.MovimentacaoService;
import com.senai.template.services.UsuarioService;
import com.senai.template.sessoes.SessaoDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

	private final UsuarioService usuarioService;
	private final CategoriaService categoriaService;
	private final ProdutoService produtoService;
	private final MovimentacaoService movimentacaoService;

	public PageController(UsuarioService usuarioService,
						 CategoriaService categoriaService,
						 ProdutoService produtoService,
						 MovimentacaoService movimentacaoService) {
		this.usuarioService = usuarioService;
		this.categoriaService = categoriaService;
		this.produtoService = produtoService;
		this.movimentacaoService = movimentacaoService;
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/home")
	public String home(HttpSession session, Model model) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);

		if (sessaoDto == null) {
			return "redirect:/login";
		}

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("isAdmin", sessaoDto.getUserRole() != null && sessaoDto.getUserRole() == 1);
		model.addAttribute("baixoEstoque", movimentacaoService.listarBaixoEstoque());

		return "home";
	}

	@GetMapping("/produtos")
	public String produtos(HttpSession session, Model model, @RequestParam(required = false) Long editar,
						@RequestParam(required = false) String busca,
						@RequestParam(required = false) String dataInicio,
						@RequestParam(required = false) String dataFim,
						@RequestParam(defaultValue = "0") int pagina) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);
		if (sessaoDto == null) return "redirect:/login";

		LocalDateTime inicio = null;
		LocalDateTime fim = null;
		try {
			if (dataInicio != null && !dataInicio.isBlank()) {
				inicio = LocalDate.parse(dataInicio).atStartOfDay();
			}
			if (dataFim != null && !dataFim.isBlank()) {
				fim = LocalDate.parse(dataFim).atTime(23, 59, 59);
			}
		} catch (Exception ignored) {
			dataInicio = "";
			dataFim = "";
		}

		if (pagina < 0) pagina = 0;
		var page = produtoService.pesquisarPaginado(
				busca == null || busca.isBlank() ? null : busca.trim(),
				inicio, fim, PageRequest.of(pagina, 10, Sort.by("nome").ascending()));

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("produtos", page.getContent());
		model.addAttribute("paginaAtual", page.getNumber());
		model.addAttribute("totalPaginas", page.getTotalPages());
		model.addAttribute("totalProdutos", page.getTotalElements());
		model.addAttribute("categorias", categoriaService.obterCategorias());
		model.addAttribute("isAdmin", sessaoDto.getUserRole() != null && sessaoDto.getUserRole() == 1);
		model.addAttribute("busca", busca == null ? "" : busca);
		model.addAttribute("dataInicio", dataInicio == null ? "" : dataInicio);
		model.addAttribute("dataFim", dataFim == null ? "" : dataFim);

		ProdutoDto produtoForm = new ProdutoDto();
		if (editar != null) {
			ProdutoDto encontrado = produtoService.buscarPorId(editar);
			if (encontrado != null) produtoForm = encontrado;
		}
		model.addAttribute("produtoForm", produtoForm);
		return "produtos";
	}

	@GetMapping("/usuarios")
	public String usuarios(HttpSession session, Model model, @RequestParam(required = false) Long editar,
							@RequestParam(required = false) String busca) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);
		if (sessaoDto == null) return "redirect:/login";

		List<UsuarioDto> usuarios = usuarioService.listarTodos();
		if (busca != null && !busca.isBlank()) {
			String termo = busca.trim().toLowerCase(Locale.ROOT);
			usuarios = usuarios.stream().filter(u ->
				(u.getNome() != null && u.getNome().toLowerCase(Locale.ROOT).contains(termo)) ||
				(u.getEmail() != null && u.getEmail().toLowerCase(Locale.ROOT).contains(termo)) ||
				(u.getUserRole() != null && (u.getUserRole() == 1 ? "administrador" : "usuário").contains(termo))
			).collect(Collectors.toList());
		}

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("isAdmin", sessaoDto.getUserRole() != null && sessaoDto.getUserRole() == 1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("busca", busca == null ? "" : busca);

		UsuarioDto usuarioForm = new UsuarioDto();
		if (editar != null) {
			UsuarioDto encontrado = usuarioService.buscarPorId(editar);
			if (encontrado != null) usuarioForm = encontrado;
		}
		model.addAttribute("usuarioForm", usuarioForm);
		return "usuarios";
	}

	@GetMapping("/estoque")
	public String estoque(HttpSession session, Model model, @RequestParam(required = false) String busca) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);
		if (sessaoDto == null) return "redirect:/login";

		List<ProdutoEntity> estoque = movimentacaoService.listarEstoqueOrdenado();
		if (busca != null && !busca.isBlank()) {
			String termo = busca.trim().toLowerCase(Locale.ROOT);
			estoque = estoque.stream().filter(p -> p.getNome() != null && p.getNome().toLowerCase(Locale.ROOT).contains(termo)).collect(Collectors.toList());
		}

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("produtos", produtoService.listarTodos());
		model.addAttribute("estoque", estoque);
		model.addAttribute("baixoEstoque", movimentacaoService.listarBaixoEstoque());
		model.addAttribute("movimentacoes", movimentacaoService.listarTodos());
		model.addAttribute("isAdmin", sessaoDto.getUserRole() != null && sessaoDto.getUserRole() == 1);
		model.addAttribute("busca", busca == null ? "" : busca);
		return "estoque";
	}

	@GetMapping("/movimentacoes")
	public String movimentacoes(HttpSession session, Model model,
							@RequestParam(required = false) String buscaProduto,
							@RequestParam(required = false) String tipo,
							@RequestParam(required = false) String buscaUsuario) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);
		if (sessaoDto == null) return "redirect:/login";

		List<MovimentacaoDto> movimentacoes = movimentacaoService.listarTodos();
		if (buscaProduto != null && !buscaProduto.isBlank()) {
			String termo = buscaProduto.trim().toLowerCase(Locale.ROOT);
			movimentacoes = movimentacoes.stream().filter(m -> m.getProdutoNome() != null && m.getProdutoNome().toLowerCase(Locale.ROOT).contains(termo)).collect(Collectors.toList());
		}
		if (tipo != null && !tipo.isBlank()) {
			movimentacoes = movimentacoes.stream().filter(m -> tipo.equalsIgnoreCase(m.getTipo())).collect(Collectors.toList());
		}
		if (buscaUsuario != null && !buscaUsuario.isBlank()) {
			String termo = buscaUsuario.trim().toLowerCase(Locale.ROOT);
			movimentacoes = movimentacoes.stream().filter(m -> m.getUsuarioNome() != null && m.getUsuarioNome().toLowerCase(Locale.ROOT).contains(termo)).collect(Collectors.toList());
		}

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("movimentacoes", movimentacoes);
		model.addAttribute("buscaProduto", buscaProduto == null ? "" : buscaProduto);
		model.addAttribute("tipo", tipo == null ? "" : tipo);
		model.addAttribute("buscaUsuario", buscaUsuario == null ? "" : buscaUsuario);
		return "movimentacoes";
	}

	@GetMapping("/categorias")
	public String categorias(HttpSession session, Model model, @RequestParam(required = false) Long editar,
							@RequestParam(required = false) String busca) {

		SessaoDto sessaoDto = SessaoUtil.ObterSessao(session);
		if (sessaoDto == null) return "redirect:/login";

		List<CategoriaDto> categorias = categoriaService.obterCategorias();
		if (busca != null && !busca.isBlank()) {
			String termo = busca.trim().toLowerCase(Locale.ROOT);
			categorias = categorias.stream().filter(c -> c.getNome() != null && c.getNome().toLowerCase(Locale.ROOT).contains(termo)).collect(Collectors.toList());
		}

		model.addAttribute("usuarioLogado", sessaoDto);
		model.addAttribute("isAdmin", sessaoDto.getUserRole() != null && sessaoDto.getUserRole() == 1);
		model.addAttribute("categorias", categorias);
		model.addAttribute("busca", busca == null ? "" : busca);

		CategoriaDto categoriaForm = new CategoriaDto();
		if (editar != null) {
			CategoriaDto encontrado = categoriaService.obterCategoriaPorId(editar);
			if (encontrado != null) categoriaForm = encontrado;
		}
		model.addAttribute("categoriaForm", categoriaForm);
		return "categorias";
	}

	@GetMapping("/categoria")
	public String categoriaLegada() {
		return "redirect:/categorias";
	}
}