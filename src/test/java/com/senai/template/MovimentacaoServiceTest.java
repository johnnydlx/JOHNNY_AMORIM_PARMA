package com.senai.template;

import com.senai.template.entities.MovimentacaoEntity;
import com.senai.template.entities.ProdutoEntity;
import com.senai.template.entities.UsuarioEntity;
import com.senai.template.repositories.MovimentacaoRepository;
import com.senai.template.repositories.ProdutoRepository;
import com.senai.template.repositories.UsuarioRepository;
import com.senai.template.services.MovimentacaoService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MovimentacaoServiceTest {

    @Test
    void deveBloquearSaidaMaiorQueEstoque() {
        ProdutoEntity produto = produtoComEstoque(5);
        UsuarioEntity usuario = usuario(1L);
        AtomicInteger movimentacoesSalvas = new AtomicInteger();

        MovimentacaoService service = service(produto, usuario, movimentacoesSalvas, new AtomicReference<>());

        String erro = service.registrar(1L, "SAIDA", 6, 1L);

        assertEquals("Saída não permitida: estoque insuficiente. Disponível: 5. Solicitado: 6.", erro);
        assertEquals(5, produto.getEstoque());
        assertEquals(0, movimentacoesSalvas.get());
    }

    @Test
    void deveRegistrarSaidaQuandoHaEstoqueSuficiente() {
        ProdutoEntity produto = produtoComEstoque(10);
        UsuarioEntity usuario = usuario(1L);
        AtomicInteger movimentacoesSalvas = new AtomicInteger();
        AtomicReference<MovimentacaoEntity> movimento = new AtomicReference<>();

        MovimentacaoService service = service(produto, usuario, movimentacoesSalvas, movimento);

        String erro = service.registrar(1L, "SAIDA", 4, 1L);

        assertNull(erro);
        assertEquals(6, produto.getEstoque());
        assertEquals(1, movimentacoesSalvas.get());
        assertEquals(10, movimento.get().getEstoqueAnterior());
        assertEquals(6, movimento.get().getEstoqueAtual());
        assertEquals(4, movimento.get().getQuantidade());
        assertSame(usuario, movimento.get().getUsuario());
    }

    @Test
    void devePermitirSaidaIgualAoEstoque() {
        ProdutoEntity produto = produtoComEstoque(7);
        UsuarioEntity usuario = usuario(1L);
        AtomicInteger movimentacoesSalvas = new AtomicInteger();
        AtomicReference<MovimentacaoEntity> movimento = new AtomicReference<>();

        MovimentacaoService service = service(produto, usuario, movimentacoesSalvas, movimento);

        String erro = service.registrar(1L, "SAIDA", 7, 1L);

        assertNull(erro);
        assertEquals(0, produto.getEstoque());
        assertEquals(1, movimentacoesSalvas.get());
        assertEquals("SAIDA", movimento.get().getTipo());
    }

    private MovimentacaoService service(ProdutoEntity produto, UsuarioEntity usuario,
                                        AtomicInteger movimentacoesSalvas,
                                        AtomicReference<MovimentacaoEntity> movimento) {
        ProdutoRepository produtos = proxy(ProdutoRepository.class, (method, args) -> {
            if (method.getName().equals("findById")) return Optional.of(produto);
            if (method.getName().equals("save")) return args[0];
            return defaultValue(method.getReturnType());
        });

        UsuarioRepository usuarios = proxy(UsuarioRepository.class, (method, args) -> {
            if (method.getName().equals("findById")) return Optional.of(usuario);
            return defaultValue(method.getReturnType());
        });

        MovimentacaoRepository movimentacoes = proxy(MovimentacaoRepository.class, (method, args) -> {
            if (method.getName().equals("save")) {
                movimentacoesSalvas.incrementAndGet();
                movimento.set((MovimentacaoEntity) args[0]);
                return args[0];
            }
            return defaultValue(method.getReturnType());
        });

        return new MovimentacaoService(movimentacoes, produtos, usuarios);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Handler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, (proxy, method, args) -> handler.handle(method, args));
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == char.class) return '\0';
        return null;
    }

    private ProdutoEntity produtoComEstoque(int estoque) {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Detergente");
        produto.setEstoque(estoque);
        produto.setEstoqueMinimo(1);
        produto.setAtivo(true);
        return produto;
    }

    private UsuarioEntity usuario(Long id) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome("Operador");
        usuario.setEmail("usuario@teste.com");
        return usuario;
    }

    @FunctionalInterface
    interface Handler {
        Object handle(java.lang.reflect.Method method, Object[] args) throws Throwable;
    }
}
