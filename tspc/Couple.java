package tspc;

/**
 *
 * @author elkrari
 */
public class Couple {
    int V1;
    int V2;
    
    public Couple(int V1, int V2){
        this.V1=V1;
        this.V2=V2;
    }
    
    public int getV1(){
        return this.V1;
    }
    public int getV2(){
        return this.V2;
    }
    
    public boolean equal(Couple C){
        return(this.V1==(C.V1) && this.V2==(C.V2));
    }

    @Override
    public String toString() {
        return "("+this.V1+","+this.V2+")";
    }
    
    
}
