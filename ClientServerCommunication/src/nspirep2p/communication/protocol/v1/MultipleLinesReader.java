package nspirep2p.communication.protocol.v1;

import java.util.ArrayList;

public class MultipleLinesReader {
    private ArrayList<String> arrayList;
    public MultipleLinesReader(){
        arrayList = new ArrayList<String>();
    }

    public void clear(){
        arrayList.clear();
    }

    public void read(String line){
        if (line != null) {
            arrayList.add(line);
        }
    }

    public boolean isEnd(){
        if (arrayList == null) return false;
        if (!(arrayList.size() > 0)) return false;
        String last = arrayList.get(arrayList.size() - 1);
        if(last == null) return false;
        return last.equals(CommunicationParser.END_WAIT) || last.equals(CommunicationParser.END_BREAK);
    }

    public String[] getLines(){
        return arrayList.toArray(new String[0]);
    }
}
