import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Fila de pedidos aguardando processamento */
    static Fila<Pedido> filaPedidos = new Fila<>();
    
    /** Pilha de produtos mais recentemente pedidos */
    static Pilha<Produto> pilhaProdutosMaisRecentes = new Pilha<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Listar pedidos na fila de processamento");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		pedido.incluirProduto(produto);
        	}
        }
    	
    	return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser enfileirado na fila de pedidos
     * e seus produtos adicionados à pilha de produtos mais recentemente pedidos.
     * @param pedido O pedido que deve ser finalizado.
     * @return true se o pedido foi finalizado com sucesso, false caso contrário
     */
    public static boolean finalizarPedido(Pedido pedido) {
    	
    	cabecalho();
    	
    	if (pedido == null) {
    		System.out.println("Erro: Nenhum pedido iniciado!");
    		return false;
    	}
    	
    	filaPedidos.enfileirar(pedido);
    	
    	// Adicionar os produtos do pedido à pilha de produtos mais recentes
    	Produto[] produtos = pedido.getProdutos();
    	int quantProdutos = pedido.getQuantosProdutos();
    	
    	for (int i = 0; i < quantProdutos; i++) {
    		if (produtos[i] != null) {
    			pilhaProdutosMaisRecentes.empilhar(produtos[i]);
    		}
    	}
    	
    	System.out.println("Pedido finalizado e enfileirado com sucesso!");
    	System.out.println("Aguardando processamento na fila...");
    	System.out.println(pedido.toString());
    	return true;
    }
    
    public static void listarProdutosPedidosRecentes() {
    	
    	cabecalho();
    	
    	if (pilhaProdutosMaisRecentes.vazia()) {
    		System.out.println("Nenhum produto foi pedido ainda!");
    		return;
    	}
    	
    	System.out.println("Produtos mais recentemente pedidos (do mais recente para o mais antigo):");
    	System.out.println("=====================================================================");
    	
    	// Usar uma pilha auxiliar para desempilhar temporariamente
    	Pilha<Produto> pilhaAuxiliar = new Pilha<>();
    	
    	// Desempilhar e imprimir
    	while (!pilhaProdutosMaisRecentes.vazia()) {
    		Produto produto = pilhaProdutosMaisRecentes.desempilhar();
    		System.out.println("  " + produto.toString());
    		pilhaAuxiliar.empilhar(produto);
    	}
    	
    	// Restaurar a pilha original
    	while (!pilhaAuxiliar.vazia()) {
    		pilhaProdutosMaisRecentes.empilhar(pilhaAuxiliar.desempilhar());
    	}
    }
    
    /**
     * Lista todos os pedidos na fila de processamento, do primeiro ao último.
     */
    public static void listarPedidosNaFila() {
    	
    	cabecalho();
    	
    	if (filaPedidos.vazia()) {
    		System.out.println("Nenhum pedido na fila de processamento no momento!");
    		return;
    	}
    	
    	System.out.println("Pedidos na fila de processamento (ordem FIFO):");
    	System.out.println("===============================================");
    	
    	// Usar uma fila auxiliar para listar os pedidos sem destruir a fila original
    	Fila<Pedido> filaAuxiliar = new Fila<>();
    	int numeroPedido = 1;
    	
    	// Desenfileirar e imprimir
    	while (!filaPedidos.vazia()) {
    		Pedido pedido = filaPedidos.desenfileirar();
    		System.out.println("\nPedido #" + numeroPedido + ":");
    		System.out.println(pedido.toString());
    		filaAuxiliar.enfileirar(pedido);
    		numeroPedido++;
    	}
    	
    	// Restaurar a fila original
    	while (!filaAuxiliar.vazia()) {
    		filaPedidos.enfileirar(filaAuxiliar.desenfileirar());
    	}
    }
    
    /**
     * Salva todos os pedidos finalizados em um arquivo de texto.
     * Cada pedido é salvo em uma linha com a data e o valor final.
     */
    public static void salvarPedidosEmArquivo() {
    	
    	String nomeArquivoPedidos = "pedidos.txt";
    	PrintWriter escritor = null;
    	
    	try {
    		escritor = new PrintWriter(new FileWriter(nomeArquivoPedidos));
    		
    		// Usar uma fila auxiliar para acessar os pedidos sem destruir a fila original
    		Fila<Pedido> filaAuxiliar = new Fila<>();
    		
    		// Desenfileirar e salvar
    		while (!filaPedidos.vazia()) {
    			Pedido pedido = filaPedidos.desenfileirar();
    			escritor.println(pedido.toString());
    			escritor.println("---"); // Separador entre pedidos
    			filaAuxiliar.enfileirar(pedido);
    		}
    		
    		// Restaurar a fila original
    		while (!filaAuxiliar.vazia()) {
    			filaPedidos.enfileirar(filaAuxiliar.desenfileirar());
    		}
    		
    		System.out.println("Pedidos salvos com sucesso no arquivo: " + nomeArquivoPedidos);
    		
    	} catch (IOException e) {
    		System.out.println("Erro ao salvar pedidos em arquivo: " + e.getMessage());
    	} finally {
    		if (escritor != null) {
    			escritor.close();
    		}
    	}
    }

    /**
     * Testa a implementação da Fila<Character> com os caracteres do nome do usuário.
     * Testa os métodos: enfileirar, desenfileirar e contar.
     */
    public static void testarFilaDeCaracteres() {
        
        System.out.println("==============================");
        System.out.println("TESTE DA FILA<CHARACTER>");
        System.out.println("==============================\n");
        
        // Criar a fila de caracteres
        Fila<Character> filaCaracteres = new Fila<>();
        
        // Nome do usuário
        String primeiroNome = "Samuel";
        String segundoNome = "Pinheiro";
        String nomeCompleto = primeiroNome + segundoNome;
        
        System.out.println("Enfileirando caracteres do nome: " + primeiroNome + " " + segundoNome);
        System.out.println("\nCaracteres a enfileirar: " + nomeCompleto);
        System.out.println();
        
        // Enfileirar os caracteres do nome
        for (char c : nomeCompleto.toCharArray()) {
            filaCaracteres.enfileirar(c);
            System.out.println("  Enfileirado: '" + c + "'");
        }
        
        System.out.println("\n--- Fila após enfileiramento ---");
        System.out.println(filaCaracteres);
        
        // Testar consultarPrimeiro
        System.out.println("Primeiro elemento da fila: '" + filaCaracteres.consultarPrimeiro() + "'\n");
        
        // Testar contagem de caracteres
        System.out.println("--- Teste de Contagem de Caracteres ---");
        char[] caracteresUnicos = {'S', 'a', 'e', 'i'};
        for (char c : caracteresUnicos) {
            int contador = filaCaracteres.contar(c);
            System.out.println("  Ocorrências de '" + c + "': " + contador);
        }
        
        // Testar desenfileiramento
        System.out.println("\n--- Teste de Desenfileiramento ---");
        System.out.println("Desenfileirando 5 elementos:");
        for (int i = 0; i < 5 && !filaCaracteres.vazia(); i++) {
            char desenfileirado = filaCaracteres.desenfileirar();
            System.out.println("  Desenfileirado: '" + desenfileirado + "'");
        }
        
        System.out.println("\n--- Fila após desenfileiramento de 5 elementos ---");
        System.out.println(filaCaracteres);
        
        // Testar contagem novamente após desenfileiramento
        System.out.println("--- Recontagem após desenfileiramento ---");
        int contador_a = filaCaracteres.contar('a');
        int contador_e = filaCaracteres.contar('e');
        System.out.println("  Ocorrências de 'a': " + contador_a);
        System.out.println("  Ocorrências de 'e': " + contador_e);
        
        System.out.println("\n==============================\n");
    }

    /**
     * Testa a implementação da Fila<Pedido> com múltiplos pedidos.
     * Demonstra enfileiramento, consulta e preservação de pedidos.
     */
    public static void testarFilaDePedidos() {
        
        System.out.println("==============================");
        System.out.println("TESTE DA FILA<PEDIDO>");
        System.out.println("==============================\n");
        
        // Criar alguns pedidos de teste
        System.out.println("Criando e enfileirando 3 pedidos de teste...\n");
        
        for (int i = 1; i <= 3; i++) {
            Pedido pedidoTeste = new Pedido(LocalDate.now(), 1);
            
            // Adicionar alguns produtos do vetor de produtos ao pedido
            if (produtosCadastrados != null && quantosProdutos > 0) {
                int indice1 = (i - 1) % quantosProdutos;
                int indice2 = (i % quantosProdutos);
                
                pedidoTeste.incluirProduto(produtosCadastrados[indice1]);
                pedidoTeste.incluirProduto(produtosCadastrados[indice2]);
            }
            
            filaPedidos.enfileirar(pedidoTeste);
            System.out.println("✓ Pedido #" + i + " enfileirado");
        }
        
        System.out.println("\n--- Fila de Pedidos Criada ---\n");
        
        // Listar os pedidos na fila
        listarPedidosNaFila();
        
        System.out.println("Total de " + filaPedidos.contar(filaPedidos.consultarPrimeiro()) 
                         + " pedidos na fila (teste de contagem).\n");
        
        System.out.println("==============================\n");
    }
    
	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
        // Executar testes da Fila<Character>
        testarFilaDeCaracteres();
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        // Executar testes da Fila<Pedido>
        testarFilaDePedidos();
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                    if (finalizarPedido(pedido)) {
                        pedido = null; // Resetar para permitir novo pedido
                    }
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> listarPedidosNaFila();
            }
            pausa();
        }while(opcao != 0);
        
        // Salvar pedidos em arquivo antes de encerrar
        if (!filaPedidos.vazia()) {
        	salvarPedidosEmArquivo();
        }

        teclado.close();    
    }
}
