import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TableProdutos extends AbstractTableModel {

	private static final long serialVersionUID = 7198190037731535460L;
	private List<Produtos> p = new ArrayList<>();
	private List<String> tbId = new ArrayList<>();
	private String colunas[] = { "Codigo", "Nome", "Quantidade", "Preco Custo", "Valor Total", "Zerar" };
	private double somaDifVar=0.00,somaDifFinal=0.00;

	@Override
	public String getColumnName(int coluna) {
		return colunas[coluna];
	}

	@Override
	public int getRowCount() {
		return p.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public void setValueAt(Object avalue, int linha, int coluna) {
		if (coluna == 5 && p.get(linha).isSelecionado() == false) {
			p.get(linha).setSelecionado(true);
			tbId.add(p.get(linha).getMfcodigo());
			somaDifVar+=p.get(linha).getResultado();
			setSomaDifFinal(somaDifVar);
			this.fireTableDataChanged();

		} else if (coluna == 5 && p.get(linha).isSelecionado() == true) {
			p.get(linha).setSelecionado(false);
			for (int i = 0; i <= (tbId.size() - 1); i++) {
				if (tbId.get(i).equals(p.get(linha).getMfcodigo()))
					tbId.remove(i);
			}
			somaDifVar-=p.get(linha).getResultado();
			setSomaDifFinal(somaDifVar);
			this.fireTableDataChanged();
		}
		

	}

	@Override
	public Object getValueAt(int linha, int coluna) {

		switch (coluna) {
		case 0:
			return p.get(linha).getMfcodigo();

		case 1:
			return p.get(linha).getNome();

		case 2:
			return p.get(linha).getQtdestoq();
		case 3:
			return p.get(linha).getPrecocus();
		case 4:
			return p.get(linha).getResultado();
		case 5:
			return p.get(linha).isSelecionado();
		}
		return null;

	}

	@Override
	public Class<?> getColumnClass(int coluna) {

		switch (coluna) {

		case 2:
			return Double.class;
		case 3:
			return Double.class;
		case 4:
			return Double.class;

		case 5:
			return Boolean.class;
		}
		
		return String.class;

	}

	public boolean isCellEditable(int linha, int coluna) {
		if (coluna == 5) {
			return true;

		}
		return false;
	}

	public void addRow(Produtos p) {
		this.p.add(p);
		this.fireTableDataChanged();

	}

	public void cleanRow(int linha) {
		this.p.remove(linha);
		this.fireTableDataChanged();

	}

	public double getSomaDifFinal() {
		return somaDifFinal;
	}

	public void setSomaDifFinal(double somaDifFinal) {
		this.somaDifFinal = somaDifFinal;
	}

	public void setSomaDifVar(double somaDifVar) {
		this.somaDifVar = somaDifVar;
	}
	
	
	

}
