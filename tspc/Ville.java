package tspc;

import java.util.Scanner;

/**
 *
 * @author elkrari
 */
public class Ville {
    int nom;
    float x,y;
    
    public Ville(){}
    
    public Ville(int nom, float x, float y){
        this.nom=nom;
        this.x=x;
        this.y=y;
    }
    
    public Ville(float x, float y){
        this.x=x;
        this.y=y;
    }
    
    public Ville(int nom){
        this.nom=nom;
    }
    
//    public boolean equals(Ville V){
//        return (this.nom==V.nom /*&& this.x==V.x && this.y==V.y*/);
//    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ville other = (Ville) obj;
        if (this.nom != other.nom) {
            return false;
        }
        return true;
    }
    
    

    public int getNom() {
        return nom;
    }

    public void setNom(int nom) {
        this.nom = nom;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    public Ville setVille(Scanner br){
        this.nom=br.nextInt();
        this.x=br.nextFloat();
        this.y=br.nextFloat();
        return this;
    }
    
    public Ville setVille(Ville V){
        this.nom=V.nom;
        this.x=V.x;
        this.y=V.y;
        return this;
    }

    @Override
    public String toString() {
        return "Ville " + nom + " (" + x + "," + y + ")";
    }
    
    
    
    
    
}
