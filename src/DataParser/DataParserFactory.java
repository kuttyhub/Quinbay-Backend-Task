package DataParser;

public class DataParserFactory {
    public DataParser getParserObject(String type){
        switch (type){
            case "txt":
                return new TXTFileParser();
            case "json":
                return new JSONFileParser();
            case "csv":
                return new CSVFileParser();
            default:
                return null;
        }
    }
}
