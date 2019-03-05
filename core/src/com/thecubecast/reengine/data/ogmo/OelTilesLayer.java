package com.thecubecast.reengine.data.ogmo;

public class OelTilesLayer extends OelLayer {

    private int[][] Cells;
    private String Tileset;

    public OelTilesLayer(int Width, int Height, String Name, String Tileset, String CSV) {
        super(Width, Height, Name);

        this.Tileset = Tileset;

        Cells = new int[Width][Height];

        //Prepare BitString
        String PreparedBitString = CSV.replace("\n", ",");
        PreparedBitString = PreparedBitString.replace(" ", "");

        String[] Bits = PreparedBitString.split(",");

        int index = 0;
        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {

                Cells[y][x] = Integer.parseInt(Bits[index]);

                index++;
            }
        }
    }


    public int getCell(int x, int y) {
        return Cells[y][x];
    }

    public String SerializetoXml() {
        return toString();
    }

    @Override
    public String toString() {
        String BitString = "<" + getName() + "tileset=\"" + getTileset() + "\" exportMode=\"CSV\">\n";
        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                BitString += Cells[y][x];
            }
            BitString += "\n";
        }
        return BitString + "<" + getName() + ">";
    }

    public String getTileset() {
        return Tileset;
    }
}
