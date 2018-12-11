import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceRoyale;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;

/** Ajuste de preço de custo para chegar ao resultado do inventario
 * sql = "update materpdv set qtdestoq=((round((qtdestoq*" +
 * Double.parseDouble(txtProcInv.getText()) + ")/" + txtResultado.getText() +
 * ",2))) where qtdestoq>0.00 and precocus>0.00 and pesavel=" + "'S'";
 * cn.executeAtualizacao(sql);
 **/

public class Main extends JFrame {

	private static final long serialVersionUID = 6376445645741759956L;
	private JPanel contentPane;
	private final JFileChooser openFileChooser;
	private JButton btnOpenDB, btnTestConnection, btnZerar, btnProcessar, btnTotalEmEstoque, btnExportarSQL;
	private JTextField txtSearch;
	private String MyDatabase, url, user, password, driver, sql, verCodigo, codZerar;
	private JTextField txtProcInv;
	private JLabel lblItensAZerar, labelDif;
	private JTable tbResultado;
	private JTextField txtZerar, txtEstMax, txtResultado;
	private JCheckBox chckbxMarcarTodos, chBoxDif;
	private double vlr, vlr2, vlrFinal, totalItem;
	private int contador = 0, cont = 0;
	private BigDecimal vlrBD;
	private JProgressBar pb;
	private Thread teste, teste2;

	DefaultTableCellRenderer centro = new DefaultTableCellRenderer();

	ArrayList<String> resultInventario = new ArrayList<String>();

	Conexao cn = new Conexao();
	TableProdutos lista = new TableProdutos();
	Produtos p = new Produtos();

	public void chamaConexao() {

		url = "jdbc:firebirdsql:localhost/3050:" + MyDatabase + "?encoding=ISO8859_1";
		driver = "org.firebirdsql.jdbc.FBDriver";
		user = "sysdba";
		password = "masterkey";
	}

	public void ExibirTodos() {
		cn.conecta(url, driver, user, password);
		sql = "Select mfcodigo,nome,qtdestoq,precocus,(qtdestoq * precocus) as Total_Item from materpdv where qtdestoq>0.00 order by nome";
		cn.executaConsulta(sql);

		centro.setHorizontalAlignment(SwingConstants.CENTER);
		tbResultado.getColumnModel().getColumn(0).setCellRenderer(centro);

		while (lista.getRowCount() > 0) {
			lista.cleanRow(0);
		}

		try {

			while (cn.rs.next()) {

				Produtos p = new Produtos();

				p.setMfcodigo(cn.rs.getString("mfcodigo"));
				p.setNome(cn.rs.getString("nome"));
				p.setQtdestoq(cn.rs.getDouble("qtdestoq"));
				p.setPrecocus(cn.rs.getDouble("precocus"));
				p.setResultado(cn.rs.getDouble("Total_Item"));
				lista.addRow(p);
			}
		} catch (SQLException ex) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Erro ao consulta banco de dados " + ex);
		}
		cn.desconecta();
		tbResultado.setModel(lista);

	}

	public void MontarLista() {
		if (!"".equals(txtZerar.getText())) {

			cn.conecta(url, driver, user, password);

			sql = "Select mfcodigo,nome,qtdestoq,precocus,(qtdestoq * precocus) as Total_Item from materpdv where upper (nome) "
					+ "like '%" + txtZerar.getText().toUpperCase() + "%'" + " and qtdestoq>0.00 order by nome";

			cn.executaConsulta(sql);

			while (lista.getRowCount() > 0) {
				lista.cleanRow(0);
			}

			try {
				while (cn.rs.next()) {
					Produtos p = new Produtos();

					p.setMfcodigo(cn.rs.getString("mfcodigo"));
					p.setNome(cn.rs.getString("nome"));
					p.setQtdestoq(cn.rs.getDouble("qtdestoq"));
					p.setPrecocus(cn.rs.getDouble("precocus"));
					p.setResultado(cn.rs.getDouble("Total_Item"));
					lista.addRow(p);

				}
			} catch (SQLException ex) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(null, "Erro ao consulta banco de dados " + ex);
			}
			cn.desconecta();
			tbResultado.setModel(lista);

		} else {

			ExibirTodos();
		}

		tbResultado.setModel(lista);

	}

	public void ZerarManual() {
		chamaConexao();
		cn.conecta(url, driver, user, password);
		chckbxMarcarTodos.setSelected(false);

		for (int i = 0; i < lista.getRowCount(); i++) {
			do {
				if (String.valueOf(lista.getValueAt(i, 5)) == "true") {

					sql = "update materpdv set qtdestoq=0.00 where mfcodigo=" + "'" + lista.getValueAt(i, 0) + "'";
					cn.executeAtualizacao(sql);
					// Criar uma lista para armazenar estes codigos
					codZerar += "'" + lista.getValueAt(i, 0) + "'" + ",";
					lista.cleanRow(i);

				}
			} while (String.valueOf(lista.getValueAt(i, 5)) == "true");
		}

		cn.desconecta();

	}

	public void MarcarTodos() {

		if (chckbxMarcarTodos.isSelected()) {

			for (int i = 0; i < lista.getRowCount(); i++) {
				do {

					lista.setValueAt(true, i, 5);

				} while (String.valueOf(lista.getValueAt(i, 5)) == "false");
			}
		} else {

			for (int i = 0; i < lista.getRowCount(); i++) {
				do {

					lista.setValueAt(false, i, 5);

				} while (String.valueOf(lista.getValueAt(i, 5)) == "true");
			}

		}

	}

	public void CorrigePrecoCusto() {
		chamaConexao();
		cn.conecta(url, driver, user, password);
		sql = "update materpdv set precocus=(Case When precocus>0.00 And "
				+ "precocus <= precofab And precocus <=prccumed And precocus <=prccucon And precocus <=pccumeco And precocus<=precoven Then precocus "
				+ "When precofab > 0.00 and precofab <= precocus And precofab <=prccumed And precofab <=prccucon And precofab <=pccumeco And precofab <=precoven Then precofab "
				+ "When prccumed > 0.00 and prccumed <= precocus And prccumed <=precofab And prccumed <=prccucon And prccumed <=pccumeco And prccumed <=precoven Then prccumed "
				+ "When prccucon > 0.00 and prccucon <= precocus And prccucon <=precofab And prccucon <=prccumed And prccucon <=pccumeco And prccucon <=precoven Then prccucon "
				+ "when pccumeco > 0.00 and pccumeco <= precocus And pccumeco <=precofab And pccumeco <=prccumed And pccumeco <=prccucon And pccumeco <=precoven Then pccumeco "
				+ "when precoven > 0.00 and precoven <= precocus And precoven <=precofab And precoven <=prccumed And precoven <=prccucon And precoven <=pccumeco Then precoven "
				+ "else case when precocus >0.00 then precocus when precofab>0.00 then precofab when prccumed>0.00 then prccumed "
				+ "when prccucon>0.00 then prccucon when pccumeco>0.00 then pccumeco else precoven End End) where PRECOCUS<>0.00 OR PRECOFAB<>0.00 OR PRCCUMED<>0.00 OR "
				+ "PRCCUCON<>0.00 OR PCCUMECO<>0.00";
		cn.executeAtualizacao(sql);
		cn.desconecta();
		ExibirTodos();

	}

	public void CorrigeEstoque() {
		chamaConexao();
		cn.conecta(url, driver, user, password);

		sql = "UPDATE MATERPDV SET QTDESTOQ=0.00 WHERE QTDESTOQ<0.00 OR QTDESTOQ IS NULL "
				+ "OR (PRECOCUS=0.00 AND PRECOFAB=0.00 AND PRCCUMED=0.00 AND PRCCUCON=0.00 AND PCCUMECO=0.00) OR (TIPOITEM=2 OR TIPOITEM=7 OR TIPOITEM=8 OR "
				+ "TIPOITEM=9 OR TIPOITEM=10 OR TIPOITEM=99) OR SITUACAO='I'";

		cn.executeAtualizacao(sql);

		cn.desconecta();
		ExibirTodos();

	}

	public void mostraResultado() {
		chamaConexao();
		cn.conecta(url, driver, user, password);
		sql = "SELECT round(SUM (precocus * qtdestoq),2) as Total_Estoque FROM MATERPDV where qtdestoq>0.00";
		cn.executaConsulta(sql);

		try {
			while (cn.rs.next()) {
				// Valor em BigDecimal
				vlrBD = cn.rs.getBigDecimal("Total_Estoque");
				txtResultado.setText(vlrBD.setScale(2, RoundingMode.HALF_EVEN).toString());

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cn.desconecta();

	}

	public void EstoqueMaximo() {
		chamaConexao();
		cn.conecta(url, driver, user, password);
		sql = "update materpdv set qtdestoq=(floor(RAND()*(((" + txtEstMax.getText() + ")-"
				+ "(1)) + 1) + (1)))where qtdestoq>" + txtEstMax.getText();
		cn.executeAtualizacao(sql);
		cn.desconecta();
		ExibirTodos();
		txtEstMax.setText("");
	}

	public void CorrigeDiferenca(double v) {
		sql = "select mfcodigo,(precocus*qtdestoq) as total_item from materpdv where qtdestoq>0.00 order by total_item desc";
		cn.executaConsulta(sql);

		try {
			while (cn.rs.next()) {
				verCodigo = cn.rs.getString("mfcodigo");
				totalItem = Double.parseDouble(cn.rs.getString("total_item"));
				break;

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (v < 0.00) {

			v = Math.abs(v);

		} else {

			if (v > totalItem) {

				JOptionPane.showMessageDialog(null,
						"N�o ser� possivel corrigir at� o final.\nO valor "
								+ BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_EVEN)
								+ " ter� que ser retirado manualmente.");
				return;

			} else {

				v *= -1;
			}

		}

		sql = "update materpdv set precocus=(select(((precocus*qtdestoq) + " + v
				+ " )/qtdestoq) from materpdv where mfcodigo= " + "'" + verCodigo + "'" + ") where mfcodigo=" + "'"
				+ verCodigo + "'";
		cn.executeAtualizacao(sql);
		verCodigo = "";

	}

	public void ProcessarInventario() {

		pb.setValue(0);

		SwingWorker worker = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub

				do {

					mostraResultado();
					chamaConexao();
					cn.conecta(url, driver, user, password);
					cont += 1;
					teste = new Thread();
					teste.sleep(10);
					pb.setValue(cont);

					sql = "update materpdv set qtdestoq=((round((qtdestoq*" + Double.parseDouble(txtProcInv.getText())
							+ ")/" + txtResultado.getText() + ",0))) where qtdestoq>0.00 and precocus>0.00";
					cn.executeAtualizacao(sql);
					sql = "SELECT round(SUM (precocus * qtdestoq),2) as Total_Estoque FROM MATERPDV where qtdestoq>0.00";
					cn.executaConsulta(sql);
					vlr = Double.parseDouble((txtProcInv.getText()));

					try {

						while (cn.rs.next() && vlr != vlr2) {

							vlr2 = cn.rs.getDouble("Total_Estoque");

							if (vlrFinal != (Math.abs(vlr2 - vlr))) {
								vlrFinal = (Math.abs(vlr2 - vlr));

							} else {
								vlrFinal = vlr2 - vlr;
								CorrigeDiferenca(vlrFinal);
								contador += 1;

							}

						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cn.desconecta();

				} while (contador == 0);

				return null;
			}

			protected void done() {

				try {
					pb.setValue(100);
					ExibirTodos();
					ExportarSQL();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		worker.execute();

	}

	public void ExportarSQL() throws IOException {
		int opcao = JOptionPane.showConfirmDialog(null, "Deseja exportar o sql dos produtos zerados manualmente?",
				"Exportar SQL", JOptionPane.YES_OPTION);
		if (opcao == JOptionPane.YES_OPTION)
			;
		{

			FileWriter listSql = new FileWriter("C:/CHART/DBCOMUM/sqlZerar.txt");
			PrintWriter gravarList = new PrintWriter(listSql);
			gravarList.println("update materpdv set qtdestoq=0.00 where mfcodigo in (" + codZerar + ")");
			listSql.close();
			JOptionPane.showMessageDialog(null, "Salvo na pasta DBCOMUM");
		}
	}

	// Consulta TableProdutos para saber valor total dos produtos marcados
	public void TesteIsso() {

		SwingWorker worker2 = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub

				lista.setSomaDifFinal(0.00);
				lista.setSomaDifVar(0.00);

				while (chBoxDif.isSelected()) {
					teste2 = new Thread();
					teste2.sleep(1000);
					labelDif.setText(String.valueOf(String.format("%.2f", lista.getSomaDifFinal())));

				}

				return null;
			}

			protected void done() {

				try {
					teste2.interrupt();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};

		worker2.execute();
	}
	
	
	public void ZerarProdSPrecoCusto() {
		
		chamaConexao();
		cn.conecta(url, driver, user, password);

		sql = "UPDATE MATERPDV SET QTDESTOQ=0.00 WHERE PRECOCUS=0.00 OR PRECOCUS IS NULL";

		cn.executeAtualizacao(sql);

		cn.desconecta();
		ExibirTodos();
		
	}
	

	/**
	 * Create the frame.
	 */

	public Main() {
		setTitle("Recalcular Inventario");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 715, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		openFileChooser = new JFileChooser();
		openFileChooser.setCurrentDirectory(new File("C:/CHART/DBCOMUM"));
		openFileChooser.setFileFilter(new FileNameExtensionFilter("fdb", "FDB"));

		btnOpenDB = new JButton("Localizar DB");
		btnOpenDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnValue = openFileChooser.showOpenDialog(Main.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {

					try {
						MyDatabase = openFileChooser.getSelectedFile().getPath();
						txtSearch.setText(MyDatabase);
					} catch (Exception e1) {
						// TODO: handle exception
						txtSearch.setText("Invalido arquivo!");

					}

				} else {
					txtSearch.setText("Nenhum arquivo selecionado!");
				}

			}
		});

		txtSearch = new JTextField();
		txtSearch.setEditable(false);
		txtSearch.setColumns(10);

		btnTestConnection = new JButton("Testar Conexao");
		btnTestConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					chamaConexao();
					cn.conecta(url, driver, user, password);

				} catch (Exception e1) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(null, "Error. " + e1);
				}

			}

		});

		btnTotalEmEstoque = new JButton("Total em Estoque");
		btnTotalEmEstoque.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mostraResultado();
			}
		});

		JLabel lblValorDoInventario = new JLabel("Valor do Inventario");

		txtProcInv = new JTextField();
		txtProcInv.setColumns(10);
		txtResultado = new JTextField();
		txtResultado.setColumns(10);
		txtResultado.setEditable(false);

		JButton btnCorrigeEstoque = new JButton("Retirar Qtde Excedente");
		btnCorrigeEstoque.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CorrigeEstoque();
			}
		});

		lblItensAZerar = new JLabel("Itens a zerar");
		lblItensAZerar.setVerticalAlignment(SwingConstants.TOP);

		btnZerar = new JButton("Zerar");
		btnZerar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZerarManual();
			}
		});

		btnProcessar = new JButton("Processar");
		btnProcessar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcessarInventario();
			}
		});

		JButton btnAjPrecoCusto = new JButton("Deixar Menor Pre\u00E7o de Custo");
		btnAjPrecoCusto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CorrigePrecoCusto();

			}
		});

		JLabel lblRs = new JLabel("R$");
		lblRs.setFont(new Font("Dialog", Font.BOLD, 14));

		txtZerar = new JTextField();
		txtZerar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				MontarLista();
			}
		});
		txtZerar.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();

		txtEstMax = new JTextField();
		txtEstMax.setColumns(10);

		JLabel lblValorMaximo = new JLabel("Estoque Maximo");

		JButton btnAplicar = new JButton("Aplicar");
		btnAplicar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EstoqueMaximo();
			}
		});

		tbResultado = new JTable();
		tbResultado.setAutoCreateRowSorter(true);
		scrollPane.setViewportView(tbResultado);

		tbResultado.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {

			}
		});

		JButton btnExportarSql = new JButton("Exportar SQL");
		btnExportarSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ExportarSQL();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		chckbxMarcarTodos = new JCheckBox("Marcar todos");
		chckbxMarcarTodos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MarcarTodos();
			}
		});

		pb = new JProgressBar();
		pb.setStringPainted(true);

		labelDif = new JLabel(" 0.00 ");
		labelDif.setBorder(new LineBorder(new Color(0, 0, 0)));

		chBoxDif = new JCheckBox("Calcular Diferen\u00E7a");
		chBoxDif.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				TesteIsso();
			}
		});
		
		JButton btnZerarProdutoS = new JButton("Zerar Produto S/ Pre\u00E7o de Custo");
		btnZerarProdutoS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				ZerarProdSPrecoCusto();
				
			}
		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(pb, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnTestConnection))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblValorMaximo)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtEstMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnAplicar)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(chckbxMarcarTodos))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblItensAZerar)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(txtZerar, 476, 476, 476)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnZerar))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblValorDoInventario)
							.addGap(4)
							.addComponent(txtProcInv, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnProcessar))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnExportarSql))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnTotalEmEstoque)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblRs)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtResultado, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnOpenDB)
									.addGap(18)
									.addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 455, GroupLayout.PREFERRED_SIZE)))))
					.addGap(16))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCorrigeEstoque)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAjPrecoCusto)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnZerarProdutoS)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelDif, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(chBoxDif)))
					.addContainerGap(72, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOpenDB)
						.addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTestConnection)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnTotalEmEstoque)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblRs)
								.addComponent(txtResultado, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(2)))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCorrigeEstoque)
						.addComponent(btnZerarProdutoS)
						.addComponent(labelDif, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(chBoxDif))
					.addGap(7)
					.addComponent(btnAjPrecoCusto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnZerar)
						.addComponent(txtZerar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblItensAZerar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtEstMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblValorMaximo)
						.addComponent(btnAplicar)
						.addComponent(chckbxMarcarTodos))
					.addGap(22)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblValorDoInventario)
						.addComponent(txtProcInv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnProcessar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnExportarSql)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);

		tbResultado.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			PlasticLookAndFeel.setPlasticTheme(new ExperienceRoyale());
			try {
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
			} catch (InstantiationException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			} catch (UnsupportedLookAndFeelException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}