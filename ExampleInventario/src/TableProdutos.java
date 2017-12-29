import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TableProdutos extends AbstractTableModel {

	private static final long serialVersionUID = 7198190037731535460L;
	private List<Produtos> p = new ArrayList<>();
	private List<String> tbId = new ArrayList<>();
	private String colunas[] = { "Codigo", "Nome", "Quantidade", "Zerar" };


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
		if (coluna == 3 && p.get(linha).isSelecionado() == false) {
			p.get(linha).setSelecionado(true);
			tbId.add(p.get(linha).getMfcodigo());
			this.fireTableDataChanged();

		} else if (coluna == 3 && p.get(linha).isSelecionado() == true) {
			p.get(linha).setSelecionado(false);
			for (int i = 0; i <= (tbId.size() - 1); i++) {
				if (tbId.get(i).equals(p.get(linha).getMfcodigo()))
					tbId.remove(i);
			}
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
			return p.get(linha).isSelecionado();
		}
		return null;

	}

	
	@Override
	public Class<?> getColumnClass(int coluna) {
		if (coluna == 3) {
			return Boolean.class;
		}

		return String.class;

	}

	public boolean isCellEditable(int linha, int coluna) {
		if (coluna == 3) {
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

}
