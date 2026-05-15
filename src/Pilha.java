import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * 
	 * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {
		
		// validar entrada
		if (numItens < 0) {
			throw new IllegalArgumentException("O número de itens não pode ser negativo.");
		}
		
		// vazia
		if (numItens == 0) {
			return new Pilha<>();
		}
		
		// contar quantos elementos existem na pilha
		int contador = 0;
		Celula<E> temp = topo;
		while (temp != fundo) {
			contador++;
			temp = temp.getProximo();
		}
		
		// verificar se há elementos suficientes
		if (contador < numItens) {
			throw new IllegalArgumentException("A pilha não contém " + numItens + " elementos. Contém apenas " + contador + ".");
		}
		
		// desempilhar numItens elementos para uma pilha auxiliar
		Pilha<E> pilhaAuxiliar = new Pilha<>();
		for (int i = 0; i < numItens; i++) {
			pilhaAuxiliar.empilhar(desempilhar());
		}
		
		// criar a nova pilha e restaurar a original mantendo a ordem
		Pilha<E> subPilha = new Pilha<>();
		while (!pilhaAuxiliar.vazia()) {
			E elemento = pilhaAuxiliar.desempilhar();
			subPilha.empilhar(elemento);
			empilhar(elemento);
		}
		
		return subPilha;
	}
}