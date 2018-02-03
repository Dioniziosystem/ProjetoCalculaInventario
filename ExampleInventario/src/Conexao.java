import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Conexao {
	private Connection conexao;
	private Statement st;
	public ResultSet rs;

	public void conecta(String url, String driver, String user, String password) {
		try {
			Class.forName(driver);
			conexao = DriverManager.getConnection(url, user, password);
			// JOptionPane.showMessageDialog(null, "Successful Connection!");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Driver do banco nao carregado " + e);

		} catch (SQLException sqlEx) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Erro ao conectar no banco de dados " + sqlEx);
		}

	}

	public void desconecta() {
		try {
			conexao.close();
		} catch (SQLException sqlEx) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Nao foi possivel desconectar o banco de dados " + sqlEx);
		}
	}

	public void executeAtualizacao(String sql) {
		try {
			st = conexao.createStatement();
			st.executeUpdate(sql);

		} catch (SQLException sqlEx) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Nao foi possivel executar comando sql, " + sql + " Erro " + sqlEx);
		}
	}

	public void executaConsulta(String sql) {
		try {
			st = conexao.createStatement();
			rs = st.executeQuery(sql);

		} catch (SQLException sqlEx) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "Nao foi possivel executar comando sql, " + sql + " Erro " + sqlEx);

		}
	}

}
