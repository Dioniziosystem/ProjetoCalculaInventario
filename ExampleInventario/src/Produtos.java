public class Produtos {
	private String mfcodigo = "";
	private String nome = "";
	private String qtdestoq = "";
	private String precocus = "";
	private boolean selecionado = false;

	public String getPrecocus() {
		return precocus;
	}

	public void setPrecocus(String precocus) {
		this.precocus = precocus;
	}

	public String getMfcodigo() {
		return mfcodigo;
	}

	public void setMfcodigo(String mfcodigo) {
		this.mfcodigo = mfcodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getQtdestoq() {
		return qtdestoq;
	}

	public void setQtdestoq(String qtdestoq) {
		this.qtdestoq = qtdestoq;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}
