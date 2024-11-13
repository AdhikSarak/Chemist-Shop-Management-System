

//Swing Packages

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.*;
import java.sql.*;

//Main Class

class sell extends JFrame implements ActionListener
{
	private Statement st;
	private Connection con;

	//List of Labels

	private JLabel lblMedicineID;
	private JLabel lblQty;
	

	//List of TextFields

	private JTextField txtMedicineID;
	private JTextField txtMediTitle;
	private JTextField txtQty;
	private JTextField txtPrice;
	private JTextField txtStatus;

	//List of Buttons

	private JButton btnDoneSell;
	private JButton btnSell;
	private JButton btnExit;
	private JButton btnRefresh;
	private JButton btnCancel;

	//List of Panel

	private JPanel btnpanel;
	private JPanel panel;
	private JTable table;

	//Table

	private DefaultTableModel tmodel;

	//Container

	private Container cpane;

	sell()
	{	}

	sell(int choice)
	{
		switch(choice)
		{
			case 1:
				//Initial Window

				setTitle("PHARMACY MANAGEMENT SYSTEM");
				setBounds(275, 250, 450, 230);
				setResizable(false);

				//cotainer

				cpane = getContentPane();

				//components

				tmodel = new DefaultTableModel();
				table = new JTable(tmodel);
				SetColHeader();

				btnpanel = new JPanel(new GridLayout(1, 5, 5, 0));
				btnSell = new JButton ("Sell");
				//btnRevenue = new JButton ("Edit");
				//btnDelete = new JButton ("Delete");
				btnRefresh = new JButton ("Refresh");
				btnExit = new JButton ("Exit");

				btnSell.addActionListener(this);
			//	btnRevenue.addActionListener(this);
			//	btnDelete.addActionListener(this);
				btnRefresh.addActionListener(this);
				btnExit.addActionListener(this);

				btnpanel.add(btnSell);
			//	btnpanel.add(btnRevenue);
			//	btnpanel.add(btnDelete);
				btnpanel.add(btnRefresh);
				btnpanel.add(btnExit);

				cpane.add(new JScrollPane(table));
				cpane.add(btnpanel, BorderLayout.SOUTH);
				break;
			case 2:
				//Appending Window

				setTitle("Confirmation of Selling data...");
				setBounds(275, 275, 400, 200);
				setResizable(false);

				//cotainer

				cpane = getContentPane();
				cpane.setLayout(new FlowLayout());

				//Components

				panel = new JPanel(new GridLayout(4, 1, 5, 0));

				lblMedicineID = new JLabel(" MediID ");
				//lblMediTitle = new JLabel(" MediTitle ");
				lblQty = new JLabel(" Qty ");
				//lblPrice = new JLabel(" Price ");

				txtMedicineID = new JTextField(15);
				//txtMediTitle = new JTextField(15);
				txtQty = new JTextField(15);
				//txtPrice = new JTextField(15);

				panel.add(lblMedicineID);
				panel.add(txtMedicineID);
				//panel.add(lblMediTitle);
				//panel.add(txtMediTitle);
				panel.add(lblQty);
				panel.add(txtQty);
			//	panel.add(lblPrice);
			//	panel.add(txtPrice);

				btnpanel = new JPanel(new GridLayout(3, 1, 10, 5));
				txtStatus = new JTextField("Status");
				btnDoneSell = new JButton ("Save");
				btnCancel = new JButton ("Cancel");

				btnDoneSell.addActionListener(this);
				btnCancel.addActionListener(this);

				btnpanel.add(txtStatus);
				btnpanel.add(btnDoneSell);
				btnpanel.add(btnCancel);

				cpane.add(panel);
				cpane.add(btnpanel, BorderLayout.EAST);
				break;
		}
		//Window Properties

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);

		//conection to DataBase

		ConnectToDatabase();
	}

	public void actionPerformed (ActionEvent ae)
	{
		if (ae.getSource() == btnSell)
		{
			new sell(2);
		}
		else if (ae.getSource() == btnExit)
		{
			System.exit(0);
		}
		else if (ae.getSource() == btnCancel)
		{
			setVisible(false);
		}
		else if(ae.getSource() == btnDoneSell)
		{
			SellData(txtMedicineID.getText(), txtQty.getText());
			txtMedicineID.setText("");
			txtMediTitle.setText("");
			txtQty.setText("");
			txtPrice.setText("");
		}
		
		else if(ae.getSource() == btnRefresh)
		{
			RetrieveData();
		}
	}

	public static void main(String arg[])
	{
		sell c = new sell(1);
		c.RetrieveData();
	}

	private void SetColHeader()
	{
		tmodel.addColumn("MedicineID");
		tmodel.addColumn("MediTitle");
		tmodel.addColumn("Qty");
		tmodel.addColumn("Price");

	}

	private void ConnectToDatabase ()
	{
		try
		{
			String dsn = "medidb";

			//load driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			//make connection
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dsn, "root", "");

			//create statement
			st = con.createStatement();
		}
		catch (Exception e) {System.out.println(e);}
	}

	private void SellData (String code, String hour)
	{
		try
		{
			String qryg = "select Qty from MediInfo where MedicineId = ?";

			PreparedStatement qs = con.prepareStatement(qryg);
			qs.setString(1, code);
			ResultSet rp = qs.executeQuery();
			String qty = new String();
			while (rp.next())
			{
				qty = rp.getString(1);
			}
			System.out.println(qty);
			System.out.println((Integer.parseInt(qty)-Integer.parseInt(hour)));
			if((Integer.parseInt(qty)-Integer.parseInt(hour)) >= 0)
			{
				//prepare statement
				String qry = "Update MediInfo set Qty = Qty-? where MedicineID = ?";
				//System.out.println(code + "\t"+title+ "\t"+hour+ "\t"+price);
				//System.out.println("Read only "+con.isReadOnly());
				//System.out.println("Closed "+con.isClosed());

				PreparedStatement ps = con.prepareStatement(qry);

				ps.setString(1, hour);
				ps.setString(2, code);
				//ps.setString(2, title);
				//ps.setString(4,price);

				ps.executeUpdate();
				txtStatus.setText("Done");;
			}
			else
			{
				txtStatus.setText("Unavailable Quantity");
			}
		}
		catch(Exception e) {System.out.println(e + "\t" + e.getMessage());	}
	}

	

	private void RetrieveData ()
	{
		try
		{
			int row = tmodel.getRowCount();
			while(row > 0)
			{
				row--;
				tmodel.removeRow(row);
			}

			//execute query
			ResultSet rs = st.executeQuery("Select * from MediInfo");

			//get metadata
			ResultSetMetaData md = rs.getMetaData();
			int colcount = md.getColumnCount();

			Object[] data = new Object[colcount];
			//extracting data

			while (rs.next())
			{
				for (int i=1; i<=colcount; i++)
				{
					data[i-1] = rs.getString(i);
				}
				tmodel.addRow(data);
			}
		}
		catch(Exception e) {System.out.println(e);	}
	}
}