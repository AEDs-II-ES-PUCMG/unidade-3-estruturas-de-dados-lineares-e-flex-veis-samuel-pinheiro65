import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;
	
	Fila() {
		
		Celula<E> sentinela = new Celula<E>();
		frente = tras = sentinela;
	}
	
	public boolean vazia() {
		
		return (frente == tras);
	}
	
	public void enfileirar(E item) {
		
		Celula<E> novaCelula = new Celula<E>(item);
		
		tras.setProximo(novaCelula);
		tras = tras.getProximo();
	}
	
	public E desenfileirar() {
		
		E item = null;
		Celula<E> primeiro;
		
		item = consultarPrimeiro();
		
		primeiro = frente.getProximo();
		frente.setProximo(primeiro.getProximo());
		
		primeiro.setProximo(null);
			
		// Caso o item desenfileirado seja também o último da fila.
		if (primeiro == tras)
			tras = frente;
		
		return item;
	}
	
	public E consultarPrimeiro() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na fila!");
		}

		return frente.getProximo().getItem();

	}

    double calcularValorMedio(Function<E, Double> extrator, int quantidade){
        Celula<E> aux = frente.getProximo();
        double soma = 0.0;
        int cont = 0;

        if(vazia()){
            throw new IllegalStateException("Não é possivel calcular com a fila vazia");
        }

        if(quantidade <= 0){
            throw new IllegalStateException("Não é possivel calcular com a quantidade menor que 0");
        }

        while ((cont < quantidade) && (aux != null)) {
            soma += extrator.apply(aux.getItem());
            aux = aux.getProximo();
            cont++;
        }

        return (soma/cont);
    }

    Fila<E> filtrar(Predicate<E> condicional, int quantidade){
        
        Fila<E> filtro = new Fila<>();
        Celula<E> aux = frente.getProximo();
        int cont = 0;

        while ((cont < quantidade) && (aux != null)) {
            if(condicional.test(aux.getItem())){
                filtro.enfileirar(aux.getItem());
            }
            aux = aux.getProximo();
            cont++;
        }

        return filtro;
    }

    public int contar(E elemento) {
        Celula<E> aux = frente.getProximo();
        int contador = 0;

        while (aux != null) {
            if (aux.getItem().equals(elemento)) {
                contador++;
            }
            aux = aux.getProximo();
        }

        return contador;
    }

    /**
     * Extrai os primeiros K elementos da fila atual e os retorna em uma nova Fila.
     * Os elementos são removidos da fila de origem, respeitando a ordem FIFO.
     * Se a fila original possuir menos de K itens, extrai apenas os disponíveis, esvaziando a fila.
     * 
     * @param numItens A quantidade de elementos a extrair
     * @return Uma nova Fila contendo os elementos extraídos na ordem de chegada
     * @throws IllegalArgumentException se numItens for menor ou igual a 0
     */
    public Fila<E> extrairLote(int numItens) {
        
        if (numItens <= 0) {
            throw new IllegalArgumentException("O número de itens deve ser maior que 0");
        }
        
        Fila<E> lote = new Fila<>();
        int contador = 0;
        
        while (!this.vazia() && contador < numItens) {
            lote.enfileirar(this.desenfileirar());
            contador++;
        }
        
        return lote;
    }
	
	@Override
	public String toString() {
		
		Celula<E> aux;
		String filaTexto = new String();
		
		aux = this.frente.getProximo();
		while (aux != null) {
			filaTexto += aux.getItem() + "\n";
			aux = aux.getProximo();
		}
		return filaTexto; 	
	}
}