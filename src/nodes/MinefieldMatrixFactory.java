/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import java.util.Random;

/**
 *
 * @author Kage
 */
public class MinefieldMatrixFactory {
    
    private int[][] matrix;
    
    public int[][] getMinefieldMatrix(int size, int bombs) {

        matrix = new int[size][size];

        for(int i = 0; i < bombs; i++){
            plantRandomBomb();
        }
        
        return matrix;
    }
    
    private void plantRandomBomb(){
        
        Random random = new Random(System.currentTimeMillis());
        
        int row = random.nextInt(matrix.length);
        int col = random.nextInt(matrix.length);
        
        if(matrix[row][col] != -1){
            
            matrix[row][col] = -1;
            recalculateAt(row, col);
            
        } else 
            plantRandomBomb();
        
    }
    
    private void recalculateAt(int row, int col){
        
        incrementAt(row - 1, col - 1);
        incrementAt(row - 1, col);
        incrementAt(row - 1, col + 1);
        incrementAt(row, col - 1);
        incrementAt(row, col + 1);
        incrementAt(row + 1, col - 1);
        incrementAt(row + 1, col);
        incrementAt(row + 1, col + 1);
        
    }
    
    private void incrementAt(int row, int col){
        
        if(row > -1 && row < matrix.length &&
                col > -1 && col < matrix.length){
            
            if(matrix[row][col] != -1)
               matrix[row][col]++;
            
        }
        
    }
}
