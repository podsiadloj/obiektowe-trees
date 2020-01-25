import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Comparator;

public class TreeDisplay implements ActionListener
{
	class TreeFrame extends JFrame
	{
		class TreeNode extends JComponent
		{
			TreeNode parentNode;
			Tree tree;

			TreeNode(TreeNode parentNode, Tree tree)
			{
				this.parentNode = parentNode;
				this.tree = tree;

				this.setPreferredSize(new Dimension(NodeSize, NodeSize));

				addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						Color color;

						if (TreeFrame.this.treeNodeSelected == null || TreeFrame.this.treeNodeSelected != TreeNode.this)
						{
							if (TreeFrame.this.treeNodeSelected != null)
								TreeFrame.this.treeNodeSelected.setForeground(Color.black);

							color = Color.red;

							TreeFrame.this.treeNodeSelected = TreeNode.this;
						}
						else
						{
							color = Color.black;

							TreeFrame.this.treeNodeSelected = null;
						}

						setForeground(color);

						repaint();
					}
				});
			}

			String getTreeName()
			{
				return tree.name;
			}

			String getNodeText()
			{
				return tree.name.substring(tree.name.lastIndexOf("_") + 1);
			}

			@Override
			protected void paintComponent(Graphics graphics)
			{
				super.paintComponent(graphics);

				Font font = new Font("Serif", Font.PLAIN, 12);
				FontMetrics metrics = graphics.getFontMetrics(font);
				String text = this.getNodeText();

				int height = metrics.getHeight();
				int width = metrics.stringWidth(text);
				int circleSize = NodeSize - 1;
				int textPositionX = NodeSize / 2 - width / 2;
				int textPositionY = NodeSize / 2 + height / 3;

				graphics.setFont(font);
				graphics.drawString(text, textPositionX, textPositionY);
				graphics.drawOval(0, 0, circleSize, circleSize);
			}
		}

		class ConnectionLayer extends LayerUI<JComponent>
		{
			@Override
			public void paint(Graphics graphics, JComponent component)
			{
				super.paint(graphics, component);

				Component[] components = TreeFrame.this.treePanel.getComponents();
				int rowPanelsCount = components.length;
				int rowPanelIndex = 1;

				while (rowPanelIndex < rowPanelsCount)
				{
					JPanel rowPanel = (JPanel)components[rowPanelIndex];
					Component[] rowPanelComponents = rowPanel.getComponents();

					for (Component nodePanelComponent : rowPanelComponents)
					{
						JPanel nodePanel = (JPanel)nodePanelComponent;
						TreeNode treeNode = (TreeNode)nodePanel.getComponent(0);
						TreeNode parentTreeNode = treeNode.parentNode;
						JPanel parentNodePanel = (JPanel)parentTreeNode.getParent();
						JPanel parentRowPanel = (JPanel)parentNodePanel.getParent();

						if (parentTreeNode != null)
						{
							int parentCoordinateX = parentRowPanel.getX() + parentNodePanel.getX() + parentNodePanel.getWidth() / 2;
							int parentCoordinateY = parentRowPanel.getY() + parentNodePanel.getY() + parentNodePanel.getHeight() - (parentNodePanel.getHeight() - NodeSize) / 2;

							int childCoordinateX = rowPanel.getX() + nodePanel.getX() + nodePanel.getWidth() / 2;
							int childCoordinateY = rowPanel.getY() + nodePanel.getY() + (nodePanel.getHeight() - NodeSize) / 2;

							graphics.drawLine(parentCoordinateX, parentCoordinateY, childCoordinateX, childCoordinateY);
						}
					}

					rowPanelIndex++;
				}
			}
		}

		class TreeComparator implements Comparator<Tree>
		{
			public int compare(Tree t1, Tree t2)
			{
				String name1 = t1.name.substring(t1.name.lastIndexOf("_") + 1);
				String name2 = t2.name.substring(t2.name.lastIndexOf("_") + 1);

				return name1.compareTo(name2);
			}
		}

		final int NodeSize = 30;
		Tree tree;
		TreeNode treeNodeSelected;
		JPanel treePanel = new JPanel();

		TreeFrame(String title)
		{
			super(title);
		}

		void setTree(Tree tree)
		{
			this.tree = tree;

			Container contentPane = this.getContentPane();

			this.treePanel.removeAll();

			ConnectionLayer connectionLayer = new ConnectionLayer();
			JLayer<JComponent> frameLayer = new JLayer<JComponent>(this.treePanel, connectionLayer);

			contentPane.add(frameLayer);

			this.treePanel.setLayout(new GridLayout(10, 1));

			this.loadTree(0, null, tree);
		}

		void loadTree(int treeLevel, TreeNode parentNode, Tree currentTree)
		{
			Component[] components = this.treePanel.getComponents();
			int rowPanelsCount = components.length;

			JPanel rowPanel;

			if (treeLevel < rowPanelsCount)
				rowPanel = (JPanel)components[treeLevel];
			else
			{
				rowPanel = new JPanel();

//				rowPanel.setBorder(BorderFactory.createLineBorder(Color.green));

				this.treePanel.add(rowPanel);
			}

			TreeNode treeNode = new TreeNode(parentNode, currentTree);
			JPanel treePanel = new JPanel();

//			treePanel.setBorder(BorderFactory.createLineBorder(Color.blue));
			treePanel.add(treeNode);

			rowPanel.add(treePanel);

			treeLevel++;

			currentTree.children.sort(new TreeComparator());

			for (Tree childTree : currentTree.children)
				this.loadTree(treeLevel, treeNode, childTree);
		}
	}

	JFrame mainFrame = new JFrame("Swap Tree Nodes");
	JPanel mainPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel file1Panel = new JPanel();
	JPanel file2Panel = new JPanel();
	JLabel file1Label = new JLabel("File 1:");
	JLabel file2Label = new JLabel("File 2:");
	JTextField file1Path = new JTextField(50);
	JTextField file2Path = new JTextField(50);
	JButton file1Button = new JButton("...");
	JButton file2Button = new JButton("...");
	JButton reloadButton = new JButton("Reload");
	JButton swapButton = new JButton("Swap");
	TreeManager treeManager;
	TreeFrame tree1Frame = new TreeFrame("Tree 1");
	TreeFrame tree2Frame = new TreeFrame("Tree 2");
	File file1;
	File file2;

	TreeDisplay(TreeManager treeManager)
	{
		this.treeManager = treeManager;

		Container contentPane = mainFrame.getContentPane();

		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setMinimumSize(new Dimension(700, 160));
		this.mainFrame.setResizable(false);
		this.mainFrame.setVisible(true);

		this.tree1Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.tree1Frame.setLocationByPlatform(true);
		this.tree1Frame.setMinimumSize(new Dimension(500, 500));
		this.tree1Frame.setResizable(false);

		this.tree2Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.tree2Frame.setLocationByPlatform(true);
		this.tree2Frame.setMinimumSize(new Dimension(500, 500));
		this.tree2Frame.setResizable(false);

		this.file1Button.addActionListener(this);
		this.file1Panel.add(file1Label);
		this.file1Panel.add(file1Path);
		this.file1Panel.add(file1Button);

		this.mainPanel.add(this.file1Panel);

		this.file2Button.addActionListener(this);
		this.file2Panel.add(file2Label);
		this.file2Panel.add(file2Path);
		this.file2Panel.add(file2Button);

		this.mainPanel.add(this.file2Panel);

		this.reloadButton.addActionListener(this);
		this.buttonPanel.add(reloadButton);

		this.swapButton.addActionListener(this);
		this.buttonPanel.add(swapButton);

		this.mainPanel.add(this.buttonPanel);

		contentPane.add(this.mainPanel);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.file1Button || e.getSource() == this.file2Button)
		{
			File currentDirectory = new File(System.getProperty("user.dir"));
			JFileChooser fileChooser = new JFileChooser(currentDirectory);
			int returnVal = fileChooser.showOpenDialog(this.mainFrame);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				String filePath = file.getAbsolutePath();

				System.out.println(filePath);

				if (e.getSource() == this.file1Button)
				{
					this.file1 = file;
					this.file1Path.setText(filePath);
				}
				else
				{
					this.file2 = file;
					this.file2Path.setText(filePath);
				}

				if (this.file1 != null && this.file2 != null)
					this.reloadTree();
			}
		}
		else if (e.getSource() == this.reloadButton)
			this.reloadTree();
		else if (e.getSource() == this.swapButton)
			this.swapTree();
	}

	private void reloadTree()
	{
		try
		{
			this.treeManager.loadTrees(this.file1, this.file2);

			this.updateTree();
		}
		catch (TreeManager.InvalidInputException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	private void updateTree()
	{
		Tree tree1 = this.treeManager.getTree1();

		this.tree1Frame.setTree(tree1);

		this.tree1Frame.setVisible(true);

		Tree tree2 = this.treeManager.getTree2();

		this.tree2Frame.setTree(tree2);

		this.tree2Frame.setVisible(true);
	}

	private void swapTree()
	{
		if (this.tree1Frame.treeNodeSelected != null && this.tree2Frame.treeNodeSelected != null)
		{
			try
			{
				this.treeManager.swap(this.tree1Frame.treeNodeSelected.getTreeName(), this.tree2Frame.treeNodeSelected.getTreeName());

				this.updateTree();
			}
			catch (InvalidKeyException e)
			{
				e.printStackTrace();
			}
		}
	}
}
