package nspirep2p.communication.protocol;

import nspirep2p.communication.protocol.CommunicationParser;

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
        arrayList.add(line);
    }

    public boolean isEnd(){
        String last = arrayList.get(arrayList.size() - 1);
        return last.equals(CommunicationParser.END_WAIT) || last.equals(CommunicationParser.END_BREAK);
    }

    public String[] getLines(){
        return (String[]) arrayList.toArray();
    }
}
