/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zachsRSHelper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author zech
 */
public class selectionFrame extends javax.swing.JFrame implements Runnable {

    /**
     *  class variables
     */
    Thread thread = null;
    //private boolean mouseReleased = false;
    private int x1 = 0;
    private int y1 = 0;
    private int x2 = 0;
    private int y2 = 0;
    
    private int px2 = 0;
    private int py2 = 0;
    
    private int[] data;
    private ArrayList<Integer> redTiles = new ArrayList<Integer>();
    private ArrayList<Integer> redRemoveTiles = new ArrayList<Integer>();
    
    //inventory columns are not equal, line 1&3 are closer to the middle slighty
    //so this array will have the right proportions to accoutn for that
    private double[] invColumWidths = new double[3];
    
    
    /**
     * Creates new form selectionFrame
     */
    public selectionFrame(int[] d) {
        this.setUndecorated(true);   
        this.setOpacity(0.3f);
        //this.getContentPane().setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
        //this.setBackground(new Color(0.06f,0.06f,0.06f,0.06f));
        //this.setBackground(new Color(50, 50, 50, 0));
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        initComponents();
        data = d;
        x1 = data[0];
        y1 = data[1];
        x2 = data[2];
        y2 = data[3];
        invColumWidths[0] = 1;
        invColumWidths[1] = 2;
        invColumWidths[2] = 3;
        
    }
    
    @Override
    public void paint(Graphics g) 
    {
        if(data[4]==1){
            paintGrid(g);
        }else{
            paintSelection(g);
        }
    }
    
    public void paintGrid(Graphics g){
        System.out.println("ENTER paintGrid");
        g.setColor(java.awt.Color.GREEN);
        Graphics2D g2 =(Graphics2D)g;
        g2.setStroke(new java.awt.BasicStroke(4));
        //.drawRect(0, 0, 20, 20);
        g2.drawRect(x1,y1,(x2-x1),(y2-y1));
        System.out.printf("paintGrid x:%d y:%d x2:%d y2:%d\n",x1,y1,x2,y2);
        //draw 3 vertical lines
        for(int i=0;i<3;i++){
            int xPart = (int)((x2-x1)*(invColumWidths[i]/4.0));
            g2.drawLine(x1+xPart, y1, x1+xPart, y2);
        }
        //draw 6 horiizontal lines
        for(int i=0;i<6;i++){
            int yPart = (int)((y2-y1)*((i+1)/7.0));
            g2.drawLine(x1, y1+yPart, x2, y1+yPart);
        }
        //fill in red tiles
        for(int tile: redTiles){
            int x = getTileX(tile);
            int y = getTileY(tile);
            int width = (int)((double)(x2-x1)/4.0);
            int height = (int)((double)(y2-y1)/7.0);
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(x, y, width-1, height-1);
        }
        //clear removed red tiles
        for(int tile: redRemoveTiles){
            int x = getTileX(tile);
            int y = getTileY(tile);
            int width = (int)((double)(x2-x1)/4.0);
            int height = (int)((double)(y2-y1)/7.0);
            g2.setColor(java.awt.Color.RED);
            g2.clearRect(x, y, width-1, height-1);
        }
        redRemoveTiles.clear();
    }
    
    public void paintSelection(Graphics g){
        if(px2 != x2 || py2 != y2){
            g.clearRect(x1,y1,(px2-x1)+10,(py2-y1)+10);
        }
        px2 = x2;
        py2 = y2;
        //g.dispose();
        //try{g.wait(100);}catch(Exception e){}
        
        //g.setStroke(new java.awt.BasicStroke(2));
        g.setColor(java.awt.Color.GREEN);
        g.setFont(new java.awt.Font("Tahoma", 11, 22));
        Graphics2D g2 =(Graphics2D)g;
        g2.setStroke(new java.awt.BasicStroke(6));
        //.drawRect(0, 0, 20, 20);
        g2.drawRect(x1,y1,(x2-x1),(y2-y1));
        //g.drawRect(x1+2,y1+2,(x2-x1)+2,(y2-y1)+2);
        //g.dispose();
    }
    
    public void start() {
        if (thread == null) {
          thread = new Thread(this);
          thread.start();
        }
    }

    public void stop() {
        thread = null;
    }

    public void run() {
        while (thread != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            Point b = MouseInfo.getPointerInfo().getLocation();
            x2 = (int) b.getX();
            y2 = (int) b.getY();
            this.repaint();
        }
        thread = null;
    }
    
    public boolean selectionIsBeingChecked(){
        return data[4]==1;
    }
    
    public boolean mouseIsInsideInventory(int x, int y){
        if((x > x1 && x < x2)&&(y > y1 && y < y2))
            return true;
        return false;
    }
    
    /* which inventory tile is it on?
     * tiles numbered 0-27 with following pattern
     * 0 1 2 3
     * 4 5 6 7
     * ...
     * 24 25 26 27
     */
    public int getInventoryTile(int x, int y){
        int tile = 0;
        int column = (int)(((double)(x-x1)) / ((double)(x2-x1))*4); //this is one slick formula, I am proud of my self for coming up with it on the fly lol.
        int row = (int)(((double)(y-y1)) / ((double)(y2-y1))*7);
        tile = (row*4)+column;
        return tile;
    }
    
    public int getTileX(int tile){
        int x = 0;
        int column = tile%4;
        x = x1 + (int)((x2-x1)*(double)(column/4.0));
        return x;
    }
    
    public int getTileY(int tile){
        int y = 0;
        int row = tile/4;
        y = y1 + (int)((y2-y1)*(double)(row/7.0));
        return y;
    }
    
    public class DrawPanel extends JPanel {


        public DrawPanel() {
            setOpaque(false);
            //setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }

        /*@Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }*/
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        Point b = MouseInfo.getPointerInfo().getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();
                
        if(selectionIsBeingChecked()){ 
            if(mouseIsInsideInventory(x,y)){
                int tile = this.getInventoryTile(x,y);
                ///*debug*/System.out.printf("Tile Selected: %d \n",tile);
                // we want to add the red tile if it is not red yet.
                // but if it is already red, we want to remove it
                if(redTiles.contains(tile)){
                    redTiles.remove(redTiles.indexOf(tile));
                    redRemoveTiles.add(tile);
                    System.out.println("removing tile: "+tile);
                }else{
                    redTiles.add(tile);
                    System.out.println("adding tile: "+tile);
                }
                this.repaint();
            }else{
                this.setVisible(false);
                return;
            }
        }else{
            x1 = x;
            y1 = y;
            ///*debug*/System.out.printf("START selection x:%d y:%d\n",x1,y1);
            ///*debug*/System.out.println("starting thread");
            this.start();
            ///*debug*/System.out.println("exit mousePressed method");
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if(selectionIsBeingChecked())return; //don't run this method if selection is being "checked"
        this.stop();
        ///*debug*/System.out.printf("END selection x:%d y:%d\n",x2,y2);
        ///*debug*/System.out.println("Mouse is released, method");
        data[0] = x1;
        data[1] = y1;
        data[2] = x2;
        data[3] = y2;
        data[4] = 1;
        this.setVisible(false);
    }//GEN-LAST:event_formMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(selectionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(selectionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(selectionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(selectionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("new data array created inside selectionFrame main method");
                new selectionFrame(new int[5]).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
