package tictactoe;

import java.io.*;
import java.net.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.*;


public class Client extends Application implements Constants  {

	private boolean myturn = false;
	private char token1 = ' ';
	private char token2 = ' ';
	private Cell[][] cell = new Cell[3][3];
	private Label LTitle = new Label();
	private Label LStatus = new Label();
	private int rowChoosen;
	private int colChoosen;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private boolean Cont = true;
	private boolean wait = true;
	private String host = "localhost";
	
	
	@Override
	public void start(Stage pS) throws Exception {
		
		GridPane gp = new GridPane();
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; i++)
				gp.add(cell[i][j] = new Cell(i,j),j,i);
		
		BorderPane bp = new BorderPane();
		bp.setTop(LTitle);
		bp.setBottom(LStatus);
		
		Scene scene = new Scene(bp ,400,400);
		pS.setTitle("TicTacToe");
		pS.setScene(scene);
		pS.show();
		
		connectServer();
	}
		
		private void connectServer() 
		{
			try
			{
				Socket s = new Socket(host, 8000);
				
				fromServer = new DataInputStream(s.getInputStream());
				toServer = new DataOutputStream(s.getOutputStream());
			}
			
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			new Thread(() ->{
				try
				{
				int player = fromServer.readInt();
				
				if(player == player1)
				{
					token1 = 'X';
					token2 = 'O';
					
					Platform.runLater(() -> {
						LTitle.setText("Player 1: X");
						LStatus.setText("Waiting for Player 2");
					});
					
					fromServer.readInt();
					
					Platform.runLater(() -> 
					LStatus.setText("Player 2 joined. You Start"));
					
					myturn = true;
					
				}
				
				else if (player == player2)
				{
					token1 = 'O';
					token2 = 'X';
					
					Platform.runLater(() -> {
						LTitle.setText("Player 2: O");
						LStatus.setText("Waiting for Player 1 to Play");
					});
					
				}
				
				while(Cont)
				{
					if(player == player1)
					{
						waitforAction();
						sendMove();
						recievefromServer();
					}
					
					else if(player == player2)
					{
						recievefromServer();
						waitforAction();
						sendMove();
					}
				}
	          }
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			});
		
		}

		private void waitforAction() throws InterruptedException
		{
			while(wait)
			{
				Thread.sleep(100);
			}
			
			wait = true;
		}
			private void sendMove() throws IOException
			{
				toServer.writeInt(rowChoosen);
				toServer.writeInt(colChoosen);
			}
			
			private void recievefromServer() throws IOException
			{
				String state = fromServer.readLine();
				
				
				if(state == player1Won)
				{
					Cont = false;
					if( token1 == 'X')
					{
						Platform.runLater(() -> LStatus.setText("You WON! X"));
						
					}
					
					else if( token1 == 'O')
					{
						Platform.runLater(() -> LStatus.setText("Player 1 Won! X"));
						recieveMove();
						
					}
				}
				
				else if(state == player2Won)
				{
					Cont = false;
					if( token1 == 'O')
					{
						Platform.runLater(() -> LStatus.setText("You WON! O"));
						
					}
					
					else if( token1 == 'X')
					{
						Platform.runLater(() -> LStatus.setText("Player 2 Won! O"));
						recieveMove();
						
					}
				}
				
				if(state == Draw)
				{
					Cont = false;
					Platform.runLater(() -> LStatus.setText("Game Over, Nobody Wins"));
				
					if( token1 == 'O')
					{
						recieveMove();
						
					}
				}
					
					else
					{
						recieveMove();
						Platform.runLater(() -> LStatus.setText("Your Turn"));
						myturn = true;
					}
				}
				
				
				private void recieveMove() throws IOException
				{
					int row = fromServer.readInt();
					int col = fromServer.readInt();
					Platform.runLater(() -> cell[row][col].setToken(token2));
				}
			

		class Cell extends Pane
		{
			private int row;
			private int col;
			private char token = ' ';
			
			public Cell(int row, int col)
			{
				this.row = row;
				this.col = col;
				this.setPrefSize(2000,2000);
				setStyle("-fx-border-color: blue");
				this.setOnMouseClicked(e -> handleMouseClick());
			}

			public char getToken() {
				
				return token;
			}

			public void setToken(char t) {
			token = t;
			repaint();
			}
			
			protected void repaint()
			{
				
			}
			
			private void handleMouseClick()
			{
				
			}
		}
			
}