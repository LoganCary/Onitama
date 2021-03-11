import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Card 
{
	private String cardName;
	private int cardID;
	private boolean redCard;
	private ArrayList<Position> legalMoves = new ArrayList<Position>();
	private BufferedImage cardImg;
	private char[][] moves;

	public Card(int cardID) 
	{

		this.cardID = cardID;

		if(cardID == 1) 
		{
			cardName = "BOAR";
			redCard = true;
		} else if(cardID == 2) 
		{
			cardName = "FROG";
			redCard = true;
		} else if(cardID == 3) 
		{
			cardName = "ROOSTER";
			redCard = true;
		} else if(cardID == 4) 
		{
			cardName = "CRANE";
			redCard = false;
		} else if(cardID == 5) 
		{
			cardName = "MONKEY";
			redCard = false;
		} else if(cardID == 6) 
		{
			cardName = "DRAGON";
			redCard = true;
		} else if(cardID == 7) 
		{
			cardName = "GOOSE";
			redCard = false;
		} else if(cardID == 8) 
		{
			cardName = "TIGER";
			redCard = false;
		} else if(cardID == 9) 
		{
			cardName = "MANTIS";
			redCard = true;
		} else if(cardID == 10) 
		{
			cardName = "ELEPHANT";
			redCard = true;
		} else if(cardID == 11) 
		{
			cardName = "CRAB";
			redCard = false;
		} else if(cardID == 12) 
		{
			cardName = "RABBIT";
			redCard = false;
		} else if(cardID == 13) 
		{
			cardName = "EEL";
			redCard = false;
		} else if(cardID == 14) 
		{
			cardName = "COBRA";
			redCard = true;
		} else if(cardID == 15) 
		{
			cardName = "HORSE";
			redCard = true;
		} else if(cardID == 16) 
		{
			cardName = "OX";
			redCard = false;
		}

		setLegalMoves();

		try 
		{
			cardImg = ImageIO.read(Onitama.class.getResourceAsStream("/resources/" + cardName + ".png"));
			//cardImg = ImageIO.read(new File("src\\resources\\" + cardName + ".png"));
		} catch(Exception e) 
		{
			//e.printStackTrace();
			System.out.println("card images not implemented");
		}
	}

	private void setLegalMoves() 
	{
		//based on a piece at position (0,0)
		//by (r,c) where r is row and c is column
		if(cardName.equals("BOAR")) 		    	// * * * * *
		{						// * * O * *
			legalMoves.add(new Position(-1,0)); 	// * O X O *
			legalMoves.add(new Position(0,1));  	// * * * * *
			legalMoves.add(new Position(0,-1)); 	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','O','*','*'},		
					{'*','O','X','O','*'},
					{'*','*','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("FROG"))	    	// * * * * *
		{						// * O * * *
			legalMoves.add(new Position(1,1));  	// O * X * *
			legalMoves.add(new Position(-1,-1));	// * * * O *
			legalMoves.add(new Position(0,-2)); 	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','*','*'},		
					{'O','*','X','*','*'},
					{'*','*','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("ROOSTER"))   	// * * * * *
		{						// * * * O *
			legalMoves.add(new Position(0,-1)); 	// * O X O *
			legalMoves.add(new Position(1,-1)); 	// * O * * *
			legalMoves.add(new Position(0,1));  	// * * * * *
			legalMoves.add(new Position(-1,1));

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','*','O','*'},		
					{'*','O','X','O','*'},
					{'*','O','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("CRANE"))	    	// * * * * *
		{						// * * O * *
			legalMoves.add(new Position(1,-1)); 	// * * X * *
			legalMoves.add(new Position(1,1));  	// * O * O *
			legalMoves.add(new Position(-1,0)); 	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','O','*','*'},		
					{'*','*','X','*','*'},
					{'*','O','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("MONKEY"))    	// * * * * *
		{						// * O * O *
			legalMoves.add(new Position(-1,-1));	// * * X * *
			legalMoves.add(new Position(1,-1)); 	// * O * O *
			legalMoves.add(new Position(-1,1)); 	// * * * * *
			legalMoves.add(new Position(1,1));

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','O','*'},		
					{'*','*','X','*','*'},
					{'*','O','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("DRAGON"))    	// * * * * *
		{						// O * * * O
			legalMoves.add(new Position(-1,-2));	// * * X * *
			legalMoves.add(new Position(1,-1)); 	// * O * O *
			legalMoves.add(new Position(-1,2)); 	// * * * * *
			legalMoves.add(new Position(1,1));

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'O','*','*','*','O'},		
					{'*','*','X','*','*'},
					{'*','O','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("GOOSE")) 		// * * * * *
		{						// * O * * *
			legalMoves.add(new Position(-1,-1));	// * O X O *
			legalMoves.add(new Position(0,-1)); 	// * * * O *
			legalMoves.add(new Position(0,1));  	// * * * * *
			legalMoves.add(new Position(1,1));

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','*','*'},		
					{'*','O','X','O','*'},
					{'*','*','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("TIGER")) 		// * * O * *
		{						// * * * * *
			legalMoves.add(new Position(-2,0)); 	// * * X * *
			legalMoves.add(new Position(1,0));  	// * * O * *
								// * * * * *


			moves = new char[][] 
					{
					{'*','*','O','*','*'},
					{'*','*','*','*','*'},		
					{'*','*','X','*','*'},
					{'*','*','O','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("MANTIS"))    	// * * * * *
		{						// * O * O *
			legalMoves.add(new Position(-1,-1));	// * * X * *
			legalMoves.add(new Position(1,0));  	// * * O * *
			legalMoves.add(new Position(-1,1)); 	// * * * * *


			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','O','*'},		
					{'*','*','X','*','*'},
					{'*','*','O','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("ELEPHANT"))  	// * * * * *
		{  						// * O * O *
			legalMoves.add(new Position(0,-1)); 	// * O X O *
			legalMoves.add(new Position(-1,-1));	// * * * * *
			legalMoves.add(new Position(0,1));  	// * * * * *
			legalMoves.add(new Position(-1,1));

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','O','*'},		
					{'*','O','X','O','*'},
					{'*','*','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("CRAB")) 		// * * * * *
		{						// * * O * *
			legalMoves.add(new Position(0,-2)); 	// O * X * O
			legalMoves.add(new Position(-1,0)); 	// * * * * *
			legalMoves.add(new Position(0,2));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','O','*','*'},		
					{'O','*','X','*','O'},
					{'*','*','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("RABBIT"))    	// * * * * *
		{						// * * * O *
			legalMoves.add(new Position(1,-1)); 	// * * X * O
			legalMoves.add(new Position(-1,1)); 	// * O * * *
			legalMoves.add(new Position(0,2));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','*','O','*'},		
					{'*','*','X','*','O'},
					{'*','O','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("EEL"))       	// * * * * *
		{						// * O * * *
			legalMoves.add(new Position(-1,-1));	// * * X O *
			legalMoves.add(new Position(1,-1)); 	// * O * * *
			legalMoves.add(new Position(0,1));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','O','*','*','*'},		
					{'*','*','X','O','*'},
					{'*','O','*','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("COBRA"))     	// * * * * *
		{						// * * * O *
			legalMoves.add(new Position(0,-1)); 	// * O X * *
			legalMoves.add(new Position(-1,1)); 	// * * * O *
			legalMoves.add(new Position(1,1));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','*','O','*'},		
					{'*','O','X','*','*'},
					{'*','*','*','O','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("HORSE"))     	// * * * * *
		{						// * * O * *
			legalMoves.add(new Position(0,-1)); 	// * O X * *
			legalMoves.add(new Position(-1,0)); 	// * * O * *
			legalMoves.add(new Position(1,0));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','O','*','*'},		
					{'*','O','X','*','*'},
					{'*','*','O','*','*'},
					{'*','*','*','*','*'}
					};

		} else if(cardName.equals("OX")) 		// * * * * *
		{ 						// * * O * *
			legalMoves.add(new Position(-1,0)); 	// * * X O *
			legalMoves.add(new Position(0,1));  	// * * O * *
			legalMoves.add(new Position(1,0));  	// * * * * *

			moves = new char[][] 
					{
					{'*','*','*','*','*'},
					{'*','*','O','*','*'},		
					{'*','*','X','O','*'},
					{'*','*','O','*','*'},
					{'*','*','*','*','*'}
					};

		}
	}
	
	public int getCardID() {
		return cardID;
	}

	public String toString() {
		for(int i = 0; i < moves.length;i++)
		{
			for(int c = 0; c < moves[i].length;c++)
			{
				System.out.print(moves[i][c]+" ");
			}
			System.out.println();
		}
		System.out.println();
		return cardName + "- redCard: " + redCard + ", cardID: " + cardID + ", #legal: " + legalMoves.size();
	}

	public ArrayList<Position> getLegalMoves() {
		return legalMoves;
	}

	public String getCardName() {
		return cardName;
	}

	public boolean redCard() {
		return redCard;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public BufferedImage getImage() {
		return cardImg;
	}
}
