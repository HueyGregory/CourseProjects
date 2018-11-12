import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;

public class PrimaryKey 
{

    //public enum DataType{INT,VARCHAR,DECIMAL,BOOLEAN};
    
	private DataType columnType;
    private String columnName;
    private boolean unique = false;
    private boolean notNull = false;
    private boolean hasDefault = false;
    private String defaultValue;
    private int wholeNumberLength;
    private int fractionalLength;
    private int varcharLength;
    private boolean primaryKey;
    
    public PrimaryKey() {	
    }
    
    public String toString()
    {
	return this.columnType.name() + " " + this.columnName;
    } 
    
    public boolean getPrimaryKey() {
    	return primaryKey;
    }
    public void setPrimaryKey(boolean newStatus) {
    	primaryKey = newStatus;
    }

    public DataType getColumnType()
    {
	return this.columnType;
    }
    void setColumnType(DataType columnType)
    {
	this.columnType = columnType;
    }
    
    public String getColumnName()
    {
	return this.columnName;
    }
    void setColumnName(String name)
    {
	this.columnName = name;
    }
    
    public boolean isUnique()
    {
	return this.unique;
    }
    public void setUnique(boolean unique)
    {
	this.unique = unique;
    }
    
    public boolean isNotNull()
    {
	return this.notNull;
    }
    public void setNotNull(boolean notNull)
    {
	this.notNull = notNull;
    }
    
    public boolean getHasDefault()
    {
	return this.hasDefault;
    }
    void setHasDefault(boolean hasDefault)
    {
	this.hasDefault = hasDefault;
    }
    
    /**
     * 
     * @return default value represented as a string, if there is one
     */
    public String getDefaultValue()
    {
	return this.defaultValue;
    }
    void setDefaultValue(String defaultValue)
    {
	this.defaultValue = defaultValue;
    }
    
    /**
     * 
     * @return for a decimal column, the number of digits in the whole number, i.e. before the decimal point
     */
    public int getWholeNumberLength()
    {
	return this.wholeNumberLength;
    }
    void setWholeNumberLength(int length)
    {
	this.wholeNumberLength = length;
    }
    
    /**
     * 
     * @return for a decimal column, the number of digits in the fraction, i.e. after the decimal point
     */
    public int getFractionLength()
    {
	return this.fractionalLength;
    }
    void setFractionalLength(int length)
    {
	this.fractionalLength = length;
    }
        
    /**
     * for a varchar column, how many characters long is it
     * @return
     */
    public int getVarCharLength()
    {
	return this.varcharLength;
    }
    void setVarcharLength(int varcharLength)
    {
	this.varcharLength = varcharLength;
    }

}
