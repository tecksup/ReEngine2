package com.thecubecast.reengine.data.ogmo;

public class OelGridLayer extends OelLayer {

    private boolean[][] Cells;

    public OelGridLayer(int Width, int Height, String Name, String Bitstring) {
        super(Width, Height, Name);

        Cells = new boolean[Width][Height];

        //Prepare BitString
        String PreparedBitString = Bitstring.replace("\n", "");
        PreparedBitString = PreparedBitString.replace(" ", "");

        String[] Bits = PreparedBitString.split("");

        int index = 0;
        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                Cells[y][x] = Integer.parseInt(Bits[index]) == 1;
                index++;
            }
        }


    }

    public int getCell(int x, int y) {
        return Cells[y][x] ? 1 : 0;
    }

    public String SerializetoXml() {
        return toString();
    }

    @Override
    public String toString() {
        String BitString = "<" + getName() + " exportMode=\"Bitstring\">\n";
        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                if (Cells[y][x] == true) {
                    BitString += "1";
                } else {
                    BitString += "0";
                }
            }
            BitString += "\n";
        }
        return BitString + "<" + getName() + ">";
    }
}
