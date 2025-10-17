package com.github.acnaweb.study_apir.service;

import com.github.acnaweb.study_apir.dto.pedido.PedidoRequestCreate;
import com.github.acnaweb.study_apir.model.Item;
import com.github.acnaweb.study_apir.model.Pedido;
import com.github.acnaweb.study_apir.model.PedidoStatus;
import com.github.acnaweb.study_apir.model.Produto;
import com.github.acnaweb.study_apir.repository.PedidoRepository;
import com.github.acnaweb.study_apir.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    private PedidoRequestCreate pedidoRequestCreate;
    private Produto produto;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurando dados de exemplo
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        item = new Item();
        item.setProduto(produto);
        item.setValor(100.0);
        item.setQuantidade(2);

        pedidoRequestCreate = new PedidoRequestCreate();
        pedidoRequestCreate.setDataEntrega("2025-10-20");
        pedidoRequestCreate.setDataPedido("2025-10-15");
        pedidoRequestCreate.setItems(Arrays.asList(item));
    }

    @Test
    void testCreate() {
        // Given: Dado que a requisição de pedido foi preparada e o produto está no repositório
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Quando o método create for chamado
        Pedido pedidoCriado = pedidoService.create(pedidoRequestCreate);

        // Then: Então o pedido criado deve ter o status ABERTO e os itens associados corretamente
        assertNotNull(pedidoCriado);
        assertEquals(PedidoStatus.ABERTO, pedidoCriado.getStatus());
        assertEquals(1, pedidoCriado.getItems().size());
        assertEquals(100.0, pedidoCriado.getItems().get(0).getValor());
    }

    @Test
    void testFindById() {
        // Given: Dado que um pedido existe no repositório
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When: Quando o método findById for chamado
        Optional<Pedido> pedidoEncontrado = pedidoService.findById(1L);

        // Then: Então o pedido encontrado deve ser o mesmo que foi simulado
        assertTrue(pedidoEncontrado.isPresent());
        assertEquals(1L, pedidoEncontrado.get().getId());
    }

    @Test
    void testFindAll() {
        // Given: Dado que existem pedidos no repositório
        Pedido pedido1 = new Pedido();
        Pedido pedido2 = new Pedido();
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido1, pedido2));

        // When: Quando o método findAll for chamado
        List<Pedido> pedidos = pedidoService.findAll();

        // Then: Então a lista de pedidos deve ter 2 elementos
        assertEquals(2, pedidos.size());
    }

    @Test
    void testFindByStatus() {
        // Given: Dado que existem pedidos com status ABERTO no repositório
        Pedido pedido1 = new Pedido();
        pedido1.setStatus(PedidoStatus.ABERTO);
        Pedido pedido2 = new Pedido();
        pedido2.setStatus(PedidoStatus.FINALIZADO);
        when(pedidoRepository.findByStatus(PedidoStatus.ABERTO)).thenReturn(Arrays.asList(pedido1));

        // When: Quando o método findByStatus for chamado com status ABERTO
        List<Pedido> pedidos = pedidoService.findByStatus(PedidoStatus.ABERTO);

        // Then: Então deve retornar apenas 1 pedido com o status ABERTO
        assertEquals(1, pedidos.size());
        assertEquals(PedidoStatus.ABERTO, pedidos.get(0).getStatus());
    }

    @Test
    void testCreateProdutoInexistente() {
        // Given: Dado que o produto não existe no repositório
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then: Quando o método create for chamado, deve lançar uma exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.create(pedidoRequestCreate);
        });

        assertEquals("Produto inexistente: 1", exception.getMessage());
    }
}
