import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*	[TO BE IMPLEMENTED]	- = unimplemented;	+ = implemented
 * 	+AI/Computer player for the player to play against
 *	+welcome/instruction screen
 *	+replay option [-make a visible display for replay button]
 *	+visibly display who won at end of game
 *	-visibly display who's turn it is
 *	-make AI turns go a little slower if possible
 *	-AI does not currently attempt to land master in enemy temple
 */

/*	CURRENT KNOWN BUGS:	- = unresolved;	+ = resolved
 * 	+when an invalid move is selected picks a seemingly random move sometimes?
 * 	+blue player legal moves on wrong pieces
 * 	+sometimes move selection doesnt work
 *  +card and red piece selected then piece released in place, if only one available move, automatically moves it
 * 	+if a piece has died for X player and X player piece land on that space, that piece cant be selected
 * 	+if a piece has died for X player, other X player pieces cannot land in that spot
 * 
 *  +computer player pieces don't actually die, they just become invisible
 *  +joptionpane for win goes into top left corner when ok is clicked on
 *  +above error is a painting issue, might be a resizing issue
 *  +computer player doesn't select a card correctly, can use one cards move but always replaces first card
 *  +/-computer vs player freezes if computer only has master left
 *  +"index 0: size 0 when picking a random move for the computer sometimes
 */
public class Onitama implements MouseListener
{

//================================================================================
//	VARIABLES
//================================================================================
	private JFrame frame;
	private JPanel panel;

	private Player redPlayer,bluePlayer;

	private Board board;

	private ArrayList<ArrayList<Position>> legalMoves = new ArrayList<ArrayList<Position>>();

	private int mouseX,mouseY;

	private Position currentPos = new Position(0,0);
	private Piece currentPiece = null;

	private boolean redTurn;

	private String winner = "";

	private int selectedCard = 0;

	private boolean pressed = false;
	private boolean cardPressed = false;

	private boolean gameOver = false;
	
	private boolean PVP;
//================================================================================
//	CONSTRUCTORS - starts the game
//================================================================================

	public Onitama()
	{
		frame = new JFrame("Onitama");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new WelcomeScreen();
		
		frame.setContentPane(panel);
		
		frame.setSize(800, 800);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		try 
		{
			frame.setIconImage(ImageIO.read(Onitama.class.getResourceAsStream("/resources/red master.png")));
		}
		catch (IOException e) {e.printStackTrace();}
		
	}

//================================================================================
//	STARTUP METHODS - sets up the game
//================================================================================

	private void returnToMenu()
	{
		
		panel = new WelcomeScreen();
		
		frame.setContentPane(panel);
		
		panel.revalidate();
		frame.revalidate();
		
		gameOver = false;
		selectedCard = 0;
		winner = "";
		cardPressed = false;
		pressed = false;
		legalMoves = new ArrayList<ArrayList<Position>>();
		
	}
	
	private void startGame(boolean PVP) 
	{	//[IMPLEMENT] - !pvp (player vs computer)

		this.PVP = PVP;
		
		redPlayer = new Player(true);
		bluePlayer = new Player(false);

		setCards();
		setFirstTurn();

		//System.out.println("\nredTurn: " + redTurn);
		
		panel = new OnitamaPanel();
		panel.addMouseListener(this);

		frame.setContentPane(panel);

		frame.setSize(800, 800);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		panel.revalidate();
		frame.revalidate();
		
		if(!PVP&&!redTurn)
			setLegalMoves();

	}
	
	private void setFirstTurn() 
	{
		if(board.getCard().redCard()) 
		{
			redTurn = true;
		} else 
		{
			redTurn = false;
		}
	}

	private void setCards() 
	{
		//array of all cards
		ArrayList<Card> allCards = new ArrayList<Card>();
		for(int i = 1; i < 17; i++) 
		{
			allCards.add(new Card(i));
		}
		//select 5 random cards
		ArrayList<Card> cards = new ArrayList<Card>(5);
		for(int i = 0; i < 5; i++) 
		{
			int randIndex = (int)(Math.random()*(allCards.size())+1);
			cards.add(allCards.get(randIndex-1));
			allCards.remove(randIndex-1);
		}
		//distribute cards
		redPlayer.setCard1(cards.get(0));
		redPlayer.setCard2(cards.get(1));
		bluePlayer.setCard1(cards.get(2));
		bluePlayer.setCard2(cards.get(3));

		//System.out.println("redPlayer: " + redPlayer);
		//System.out.println("bluePlayer: " + bluePlayer);
		//System.out.println();

		board = new Board(cards.get(4));

		//System.out.println("board card: " + board.getCard());
	}

//================================================================================
//	GAMEPLAY - handles gameplay mechanics and updates
//================================================================================

	private void resetLegalMoves() 
	{
		legalMoves = new ArrayList<ArrayList<Position>>();
	}

	private void setLegalMoves() 
	{
		legalMoves = new ArrayList<ArrayList<Position>>();
		ArrayList<Position> cardMoves = new ArrayList<Position>();

		if(!gameOver) {
			if(redTurn)
			{
				if(selectedCard==1)
				{
					cardMoves = redPlayer.getCard1().getLegalMoves();
				} 
				else if(selectedCard==2)
				{
					cardMoves = redPlayer.getCard2().getLegalMoves();
				}

				for(int i = 0; i < redPlayer.getDisciples().size();i++) 
				{	
					Piece temp = redPlayer.getDisciples().get(i);
					legalMoves.add(new ArrayList<Position>());

					for(int c = 0; c < cardMoves.size(); c++) 
					{
						int tempRow = temp.getPosition().getRow()+cardMoves.get(c).getRow();
						int tempCol = temp.getPosition().getCol()+cardMoves.get(c).getCol();

						if(tempRow > -1 && tempRow < 5)
						{
							if(tempCol > -1 && tempCol < 5) 
							{
								Position tempPos = new Position(tempRow,tempCol);
								if(redPlayer.getPiece(tempPos)==null)
									legalMoves.get(i).add(tempPos);
								else if(redPlayer.getPiece(tempPos).getDead())
									legalMoves.get(i).add(tempPos);
							}
						}
					}
				}

				legalMoves.add(new ArrayList<Position>());

				for(int i = 0; i < cardMoves.size();i++) 
				{

					int tempRow = redPlayer.getMaster().getPosition().getRow()+cardMoves.get(i).getRow();
					int tempCol = redPlayer.getMaster().getPosition().getCol()+cardMoves.get(i).getCol();

					if(tempRow > -1 && tempRow < 5) 
					{
						if(tempCol > -1 && tempCol < 5) 
						{
							Position tempPos = new Position(tempRow,tempCol);
							if(redPlayer.getPiece(tempPos)==null)
								legalMoves.get(legalMoves.size()-1).add(tempPos);
							else if(redPlayer.getPiece(tempPos).getDead())
								legalMoves.get(i).add(tempPos);
						}
					}
				}

			}
			else
			{
				if(PVP)
				{
					if(selectedCard==1)
					{
						cardMoves = bluePlayer.getCard1().getLegalMoves();
					} 
					else if(selectedCard==2)
					{
						cardMoves = bluePlayer.getCard2().getLegalMoves();
					}

					for(int i = 0; i < bluePlayer.getDisciples().size();i++) 
					{	
						Piece temp = bluePlayer.getDisciples().get(i);
						legalMoves.add(new ArrayList<Position>());

						for(int c = 0; c < cardMoves.size(); c++) 
						{
							int tempRow = temp.getPosition().getRow()-cardMoves.get(c).getRow();
							int tempCol = temp.getPosition().getCol()-cardMoves.get(c).getCol();

							if(tempRow > -1 && tempRow < 5)
							{
								if(tempCol > -1 && tempCol < 5) 
								{
									Position tempPos = new Position(tempRow,tempCol);
									if(bluePlayer.getPiece(tempPos)==null)
										legalMoves.get(i).add(tempPos);
									else if(bluePlayer.getPiece(tempPos).getDead())
										legalMoves.get(i).add(tempPos);
								}
							}
						}
					}

					legalMoves.add(new ArrayList<Position>());

					for(int i = 0; i < cardMoves.size();i++) 
					{

						int tempRow = bluePlayer.getMaster().getPosition().getRow()-cardMoves.get(i).getRow();
						int tempCol = bluePlayer.getMaster().getPosition().getCol()-cardMoves.get(i).getCol();

						if(tempRow > -1 && tempRow < 5) 
						{
							if(tempCol > -1 && tempCol < 5) 
							{
								Position tempPos = new Position(tempRow,tempCol);
								if(bluePlayer.getPiece(tempPos)==null)
									legalMoves.get(legalMoves.size()-1).add(tempPos);
								else if(bluePlayer.getPiece(tempPos).getDead())
									legalMoves.get(i).add(tempPos);
							}
						}
					}
				}
				else
				{
					boolean moved = false;
					do
					{
						selectedCard++;
						cardPressed = true;
						if(selectedCard==1)
						{
							cardMoves = bluePlayer.getCard1().getLegalMoves();
						} 
						else if(selectedCard==2)
						{
							cardMoves = bluePlayer.getCard2().getLegalMoves();
						}

						for(int i = 0; i < bluePlayer.getDisciples().size();i++) 
						{	
							Piece temp = bluePlayer.getDisciples().get(i);
							legalMoves.add(new ArrayList<Position>());

							for(int c = 0; c < cardMoves.size(); c++) 
							{
								int tempRow = temp.getPosition().getRow()-cardMoves.get(c).getRow();
								int tempCol = temp.getPosition().getCol()-cardMoves.get(c).getCol();

								if(tempRow > -1 && tempRow < 5)
								{
									if(tempCol > -1 && tempCol < 5) 
									{
										Position tempPos = new Position(tempRow,tempCol);
										if(bluePlayer.getPiece(tempPos)==null)
											legalMoves.get(i).add(tempPos);
										else if(bluePlayer.getPiece(tempPos).getDead())
											legalMoves.get(i).add(tempPos);
									}
								}
							}
						}

						legalMoves.add(new ArrayList<Position>());

						for(int i = 0; i < cardMoves.size();i++) 
						{

							int tempRow = bluePlayer.getMaster().getPosition().getRow()-cardMoves.get(i).getRow();
							int tempCol = bluePlayer.getMaster().getPosition().getCol()-cardMoves.get(i).getCol();

							if(tempRow > -1 && tempRow < 5) 
							{
								if(tempCol > -1 && tempCol < 5) 
								{
									Position tempPos = new Position(tempRow,tempCol);
									if(bluePlayer.getPiece(tempPos)==null)
										legalMoves.get(legalMoves.size()-1).add(tempPos);
									else if(bluePlayer.getPiece(tempPos).getDead())
										legalMoves.get(i).add(tempPos);
								}
							}
						}
						for(int i = 0; i < bluePlayer.getDisciples().size()+1;i++)
						{
							for(int c = 0; c < legalMoves.get(i).size();c++)
							{
								if(redPlayer.getPiece(legalMoves.get(i).get(c))!=null)
								{
									pressed = true;
									if(i < bluePlayer.getDisciples().size())
										currentPiece = bluePlayer.getDisciples().get(i);
									else
										currentPiece = bluePlayer.getMaster();
									currentPos = new Position(legalMoves.get(i).get(c));

									if(checkLegalMove()&&!currentPiece.getDead())
									{
										move();
										moved = true;
									}
								}
							}
						}
						
						
					} while(selectedCard<3&&!moved);
					
					if(!moved)
					{
						pressed = true; cardPressed = true;
						
						resetLegalMoves();
						selectedCard = (int)(Math.random()*2+1);
						
						if(selectedCard==1)
						{
							cardMoves = bluePlayer.getCard1().getLegalMoves();
						} 
						else if(selectedCard==2)
						{
							cardMoves = bluePlayer.getCard2().getLegalMoves();
						}

						for(int i = 0; i < bluePlayer.getDisciples().size();i++) 
						{	
							Piece temp = bluePlayer.getDisciples().get(i);
							legalMoves.add(new ArrayList<Position>());

							for(int c = 0; c < cardMoves.size(); c++) 
							{
								int tempRow = temp.getPosition().getRow()-cardMoves.get(c).getRow();
								int tempCol = temp.getPosition().getCol()-cardMoves.get(c).getCol();

								if(tempRow > -1 && tempRow < 5)
								{
									if(tempCol > -1 && tempCol < 5) 
									{
										Position tempPos = new Position(tempRow,tempCol);
										if(bluePlayer.getPiece(tempPos)==null)
											legalMoves.get(i).add(tempPos);
										else if(bluePlayer.getPiece(tempPos).getDead())
											legalMoves.get(i).add(tempPos);
									}
								}
							}
						}

						legalMoves.add(new ArrayList<Position>());

						for(int i = 0; i < cardMoves.size();i++) 
						{

							int tempRow = bluePlayer.getMaster().getPosition().getRow()-cardMoves.get(i).getRow();
							int tempCol = bluePlayer.getMaster().getPosition().getCol()-cardMoves.get(i).getCol();

							if(tempRow > -1 && tempRow < 5) 
							{
								if(tempCol > -1 && tempCol < 5) 
								{
									Position tempPos = new Position(tempRow,tempCol);
									if(bluePlayer.getPiece(tempPos)==null)
										legalMoves.get(legalMoves.size()-1).add(tempPos);
									else if(bluePlayer.getPiece(tempPos).getDead())
										legalMoves.get(i).add(tempPos);
								}
							}
						}
						
						int randomPieceID,randomMove;
						do 
						{
							
							//System.out.println("\\\\\\\\\\\\\\\\\\\\\\");add in counting up pieces that are dead and chaning randPieceId to number of dead pieces
							int randPieceRange = (4-0)+1;
							randomPieceID = (int)(Math.random()*(randPieceRange));
							//System.out.println(randomPieceID);
							int randMoveRange = ((legalMoves.get(randomPieceID).size()-1)-0)+1;
							randomMove = (int)(Math.random()*(randMoveRange));
							//System.out.println(randomMove);

							//System.out.println(randomPieceID + " " + randomMove);

							currentPiece = bluePlayer.getPiece(randomPieceID);
						} while(currentPiece.getDead()||legalMoves.get(randomPieceID).size()<1);
						//System.out.println("SIZE: "+legalMoves.size());
						//System.out.println("PIECE ID: "+randomPieceID);
						//System.out.println("ROW: "+legalMoves.get(randomPieceID).get(randomMove).getRow()+" COLUMN: "+legalMoves.get(randomPieceID).get(randomMove).getCol());
						//legalMoves.get(randomPieceID);
						currentPos = new Position(legalMoves.get(randomPieceID).get(randomMove));
						
						if(checkLegalMove()) {
							move();
						}
					}
				}
			}
		}
	}

	
	private boolean checkLegalMove() {
		for(int i = 0; i < legalMoves.get(currentPiece.getID()).size();i++) {
			if(legalMoves.get(currentPiece.getID())!=null)
			if(currentPos.equals(legalMoves.get(currentPiece.getID()).get(i)))
				return true;

		}

		return false;
	}
	
	private void move() 
	{
		currentPiece.move(currentPos);

		Card holdCard = new Card(board.getCard().getCardID());

		if(redTurn) 
		{
			if(selectedCard == 1)
			{
				board.setCard(redPlayer.getCard1());
				redPlayer.setCard1(holdCard);
			}
			if(selectedCard == 2)
			{
				board.setCard(redPlayer.getCard2());
				redPlayer.setCard2(holdCard);
			}
		} else
		{
			if(selectedCard == 1)
			{
				board.setCard(bluePlayer.getCard1());
				bluePlayer.setCard1(holdCard);
			}
			if(selectedCard == 2)
			{
				board.setCard(bluePlayer.getCard2());
				bluePlayer.setCard2(holdCard);
			}
		}

		if(redTurn)
		{	
			if(bluePlayer.getPiece(currentPos)!=null) {
				bluePlayer.getPiece(currentPos).setDead(true);
			}
		} else
		{
			if(redPlayer.getPiece(currentPos)!=null) {
				redPlayer.getPiece(currentPos).setDead(true);
			}
		}
		
		cardPressed = false;
		currentPiece = null;
		redTurn = !redTurn;
		selectedCard = 0;
		resetLegalMoves();
		checkWin();
		panel.repaint();
		
		if(!PVP)
		{
			//System.out.println("5");
			setLegalMoves();
		}
		
	}
	
	private void checkWin() 
	{
		if(bluePlayer.getMaster().getDead())
		{
			winner = "Red win!";
			gameOver = true;
		} else if(bluePlayer.getMaster().getPosition().equals(new Position(4,2)))
		{
			winner = "Blue win!";
			gameOver = true;
		}
		if(redPlayer.getMaster().getDead())
		{
			winner = "Blue win!";
			gameOver = true;
		} else if(redPlayer.getMaster().getPosition().equals(new Position(0,2)))
		{
			winner = "Red win!";
			gameOver = true;
		}
		//gameOver = false;
	}

//================================================================================
//	GRAPHICS - handles game display
//================================================================================
	//TODO
	private class WelcomeScreen extends JPanel implements ActionListener
	{
		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private JButton playerVComputer, playerVPlayer;
		private JLabel welcome;
		private ArrayList<JLabel> rules;
		
		public WelcomeScreen()
		{
			
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gridBag = new GridBagConstraints();
			
			Font font = new Font("Papyrus", Font.PLAIN, 16);
			
			playerVComputer = new JButton("Player vs. Computer");
			//playerVComputer.setFont(font); hard to do with gridbag/would take a little time, is it worth?
			playerVComputer.setFocusPainted(false);//delete the shrug shoulders I think it might break eclipse
			playerVComputer.setActionCommand("pvc");
			playerVComputer.addActionListener(this);
			gridBag.gridx = 0;
			gridBag.gridy = 2;
			this.add(playerVComputer,gridBag);
			
			playerVPlayer = new JButton("Player vs. Player");
			playerVPlayer.setFocusPainted(false);
			playerVPlayer.setActionCommand("pvp");
			playerVPlayer.addActionListener(this);
			gridBag.gridx = 2;
			gridBag.gridy = 2;
			this.add(playerVPlayer,gridBag);
			
			gridBag.insets = new Insets(10,0,0,0);//everything is spaced out so you can actually read it
			
			font = new Font("Dialog", Font.BOLD, 25);
			
			rules = new ArrayList<JLabel>();
			
			rules.add(new JLabel("|HOW TO PLAY|"));
			rules.get(0).setForeground(Color.red);
			
			rules.add(new JLabel("RULES:"));
			rules.get(1).setForeground(Color.blue);
			
			rules.add(new JLabel("Each player (red and blue) starts with 4 pawns and 1 master. The players' pieces start"));
			rules.add(new JLabel("on their respective sides with the master in the middle(on their respective temple)"));
			rules.add(new JLabel(" and 2 pawns on each side. The masters and their temples are outlined in yellow."));
			rules.add(new JLabel("Each player starts with 2 random cards out of 16 available cards, and the board starts"));
			rules.add(new JLabel("with its own random card. Each card is either colored red or blue, which determines which"));
			
			rules.add(new JLabel("player goes first with respect to the card's color. When it is a player's turn, all of"));
			rules.add(new JLabel("his/her pieces have the same moves based on the cards that they currently have. When that"));
			rules.add(new JLabel("player moves a piece, the card the he/she used for that move is swapped with the board's card"));
			rules.add(new JLabel("and placed to the right of and facing the opponent. If a piece lands on the same space as"));
			rules.add(new JLabel("an opponent's piece, the opponent's piece is captured and taken off of the board. To win,"));
			
			rules.add(new JLabel("one player must either capture the opponent's master, or land their own master in the"));
			rules.add(new JLabel("opponent's temple."));
			
			rules.add(new JLabel("HOW TO MOVE:"));
			rules.get(14).setForeground(Color.blue);
			
			rules.add(new JLabel("1. Select the card that you would like to use"));
			rules.add(new JLabel("2. Press down on the piece that you would like to move"));
			rules.add(new JLabel("3. Drag and release that piece onto the space that you would like to move to"));
			
			for(int i = 0; i < rules.size();i++)
			{
				gridBag.gridx = 1;
				gridBag.gridy = 3+i;
				this.add(rules.get(i),gridBag);
			}
			
			welcome = new JLabel("WELCOME TO ONITAMA");
			welcome.setFont(font);
			gridBag.anchor = GridBagConstraints.PAGE_START;
			gridBag.gridx = 1;
			gridBag.gridy = 0;
			this.add(welcome,gridBag);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(arg0.getActionCommand().equals("pvc")) 
			{
				//System.out.println("[TODO]");
				startGame(false);
			}
			else if(arg0.getActionCommand().equals("pvp"))
			{
				startGame(true);
			}
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			Composite comp = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
			try {
			final BufferedImage oniBackground = ImageIO.read(Onitama.class.getResourceAsStream("/resources/japanese background.jpg"));
			//final BufferedImage oniBoard = ImageIO.read(new File("src\\resources\\japanese background.jpg"));
			g2.drawImage(oniBackground,0,0,null);
			} catch(Exception e) {
				e.printStackTrace();
			}
			g2.setComposite(comp);
		}	
	}

	//TODO
	@SuppressWarnings("serial")
	private class OnitamaPanel extends JPanel
	{

//		int boardX = 207,boardY = 203;
//		int tileHeight = 77,tileWidth = 77;

		public OnitamaPanel() 
		{

		}

		private void drawPieces(Graphics g) 
		{
			redPlayer.drawPieces(g);
			bluePlayer.drawPieces(g);
		}

		private void drawLegalMoves(Graphics g) 
		{
			
			if(redTurn)
				g.setColor(Color.red);
			else
				g.setColor(Color.blue);
			
			if(currentPiece!=null) 
			{
				for(int i = 0; i < legalMoves.get(currentPiece.getID()).size();i++) 
				{
					Position temp = legalMoves.get(currentPiece.getID()).get(i);
					g.fillRect(temp.getXCoord(),temp.getYCoord(),77,77);
				}
			}
		}

		private void drawRotateImage(int degrees, BufferedImage image, int drawLocationX,int drawLocationY,Graphics g)
		{

			double rotationRequired = Math.toRadians (degrees);
			double locationX = image.getWidth() / 2;
			double locationY = image.getHeight() / 2;
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

			// Drawing the rotated image at the required drawing locations
			g.drawImage(op.filter(image, null), drawLocationX, drawLocationY, null);

			//http://stackoverflow.com/questions/8639567/java-rotating-images
		}

		public void paintComponent(Graphics g) 
		{

			super.paintComponent(g);

			//draws board
			try {
				final BufferedImage oniBoard = ImageIO.read(Onitama.class.getResourceAsStream("/resources/board.png"));
				//final BufferedImage oniBoard = ImageIO.read(new File("src\\resources\\board.png"));
				g.drawImage(oniBoard, 0, 0, null);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//g.setColor(Color.white);
			//g.fillRect(currentPos.getXCoord(), currentPos.getYCoord(), 77, 77);

			if(selectedCard != 0)
				drawLegalMoves(g);

			//draws all player pieces
			drawPieces(g);

			//draw cards
			//blue
			g.setColor(Color.blue);
			drawRotateImage(180,bluePlayer.getCard1().getImage(),207,80,g);
			g.drawRect(207,80,191,100);  //card 1
			drawRotateImage(180,bluePlayer.getCard2().getImage(),401,80,g);
			g.drawRect(401,80,191,100);  //card 2
			//red
			g.setColor(Color.red);
			g.drawImage(redPlayer.getCard1().getImage(), 207, 611, null);
			g.drawRect(207,611,191,100);  //card 1
			g.drawImage(redPlayer.getCard2().getImage(), 401, 611, null);
			g.drawRect(401,611,191,100);  //card 2
			//board
			g.setColor(Color.white);
			if(redTurn)
			{
				g.drawImage(board.getCard().getImage(),597, 345, null);
				g.drawRect(597, 345, 191, 100);
				
				if(selectedCard==1)
				{
					g.setColor(Color.white);
					g.drawRect(207,611,191,100);
				}
				else if(selectedCard==2)
				{
					g.setColor(Color.white);
					g.drawRect(401, 611, 191, 100);
				}
			}
			else
			{
				drawRotateImage(180,board.getCard().getImage(),3,345,g);
				g.drawRect(3,345,191,100);
				
				if(selectedCard==1)
				{
					g.setColor(Color.white);
					g.drawRect(207,80,191,100);
				}
				else if(selectedCard==2)
				{
					g.setColor(Color.white);
					g.drawRect(401, 80, 191, 100);
				}
			}

			if(gameOver) 
			{
//				if(!winMessageDisplayed) 
//				{
//					winMessageDisplayed = true;
//					JOptionPane.showMessageDialog(frame,winner);
//					//panel.repaint();
//				}
//				//DRAW IMAGE ON THE BOARD THAT SAYS WHICH player WINS
				if(winner.equals("Red win!"))
				{
					try {
						g.drawImage(ImageIO.read(Onitama.class.getResourceAsStream("/resources/Red Wins.PNG")),196,600,null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if(winner.equals("Blue win!"))
				{
					try {
						g.drawImage(ImageIO.read(Onitama.class.getResourceAsStream("/resources/Blue Wins.PNG")),177,80,null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					g.drawImage(ImageIO.read(Onitama.class.getResourceAsStream("/resources/replay.png")), 300, 350, null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		}
		//image size constraints
		//pieces:	77x77
		//board:	frame size
		//cards:	191x100

		//square board places
		//top left:		(207,203)
		//bottom left:	(207,588)
		//top right:	(592,203)
		//bottom right:	(592,588)
		//squares are 77x77
		//needs to be a buffer zone of a couple pixels:i disagree

		//card places	width(191) height(100)
		//red card 1:	x(207) y(80) 
		//red card 2:	x(401) y(80)
		//blue card 1:	x(207) y(611)
		//blue card 2:	x(401) y(611)
	}

//================================================================================
//	MOUSE EVENTS - handles user interaction with the game
//================================================================================

	public void mouseClicked(MouseEvent e) {//System.out.println("x: "+e.getX()+" y: "+e.getY()); }
		
	}
	public void mouseEntered(MouseEvent e) { panel.repaint(); }
	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) 
	{

		mouseX = e.getX();
		mouseY = e.getY();

		if(!gameOver)
		{
			if(mouseX>207&&mouseX<592)  //within x bounds of board
			{
				if(mouseY>203&&mouseY<588)  //within y bounds of board
				{

					//pressed = true;

					for(int i = 0; i < 5; i++) 
					{
						if(mouseX>207+77*(i))
							currentPos.setCol(i);
						if(mouseY>203+77*(i))
							currentPos.setRow(i);

						if(redTurn) {
							currentPiece = redPlayer.getPiece(currentPos);
						} else {
							currentPiece = bluePlayer.getPiece(currentPos);
						}
					}
					if(currentPiece != null)
						if(currentPiece.getDead()) {
							//System.out.println("DEAD: "+currentPiece.getDead());
							currentPiece = null;
						}

					if(currentPiece!=null)
						pressed = true;

//					System.out.println("a");
//					System.out.println(currentPos);
//					if(currentPiece!=null)
//						System.out.println(currentPiece.printPiece());
//					else
//						System.out.println(currentPiece);

				}
			}
			panel.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();

		if(!gameOver)
		{
			
			if(mouseX>207&&mouseX<592)  //if click is within x bounds of board
			{
				if(!pressed)  //player did not select a piece
				{
					if(mouseY>611&&mouseY<711&&redTurn)  //within y bounds of red cards and red turn
					{

						if(mouseX<398)			//card 1
						{
							//System.out.println(redPlayer.getCard1());
							selectedCard = 1;
							cardPressed = true;
							setLegalMoves();
						} else if(mouseX>401)	//card 2
						{
							//System.out.println(redPlayer.getCard2());
							selectedCard = 2;
							cardPressed = true;
							setLegalMoves();
						}
					} else if(mouseY>80&&mouseY<180&&!redTurn&&PVP)  //within y bounds of blue cards and not red turn
					{

						if(mouseX<398)			//card 1
						{
							//System.out.println(bluePlayer.getCard1());
							selectedCard = 1;
							cardPressed = true;
							setLegalMoves();
						} else if(mouseX>401)	//card 2
						{
							//System.out.println(bluePlayer.getCard2());
							selectedCard = 2;
							cardPressed = true;
							setLegalMoves();
						}
					}
				} else //player has selected a piece
				{
					if(mouseY>203&&mouseY<588)  //within y bounds of board
					{

						pressed = false;

						for(int i = 0; i < 5; i++) 
						{
							if(mouseX>207+77*(i))
								currentPos.setCol(i);
							if(mouseY>203+77*(i))
								currentPos.setRow(i);

						}

						if(cardPressed)
							if(checkLegalMove())
								move();

//						System.out.println("b");
//						System.out.println(currentPos);
//						if(currentPiece!=null)
//							System.out.println(currentPiece.printPiece());
//						else
//							System.out.println(currentPiece);

					} else  //not released within y bounds of board
					{
						currentPiece = null;
						currentPos = new Position(0,0);

//						System.out.println("c");
//						System.out.println(currentPos);
//						if(currentPiece!=null)
//							System.out.println(currentPiece.printPiece());
//						else
//							System.out.println(currentPiece);
//						System.out.println();
					}

				}
			} else
			{
				currentPiece = null;
				currentPos = new Position(0,0);

//				System.out.println("c");
//				System.out.println(currentPos);
//				if(currentPiece!=null)
//					System.out.println(currentPiece.printPiece());
//				else
//					System.out.println(currentPiece);
//				System.out.println();
			}

			pressed = false;

			panel.repaint();
		} else {
			if(mouseX>300&&mouseX<500)
			{
				if(mouseY>350&&mouseY<450)
				{
					//System.out.println("D");
					returnToMenu();
				}
			}
		}
	}

//================================================================================
//	MAIN
//================================================================================


	public static void main(String[] args) {

		new Onitama();

	}

}
