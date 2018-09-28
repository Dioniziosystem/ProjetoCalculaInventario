
public class Produtos {
	private String mfcodigo = "";
	private String nome = "";
	private double qtdestoq =0.00;
	private double precocus =0.00;
	private double resultado=0.00;
	private boolean selecionado = false;



	public double getResultado() {
		return resultado;
	}

	public void setResultado(double resultado) {
		this.resultado = resultado;
	}

	public double getPrecocus() {
		return precocus;
	}

	public void setPrecocus(double precocus) {
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

	public double getQtdestoq() {
		return qtdestoq;
	}

	public void setQtdestoq(double qtdestoq) {
		this.qtdestoq = qtdestoq;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}
