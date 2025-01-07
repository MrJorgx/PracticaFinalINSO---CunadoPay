package models;
public class ReportVO {
    private final int idWorker;
    private final Date date,
    private final String name, description;
    //Declaracion
    public Report(String name, Date date, String description, int idWorker) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.idWorker = idWorker;
    }
    //GET
    public String getName() {
        return this.name;
    }
    public Date getDate() {
        return this.date;
    }
    public String getDescription() {
        return this.description;
    }
    public int getIdWorker() {
        return this.idWorker;
    }
    //SET
    public void setName(String name) {
        this.id=id;
    }
    public void setDate(Date date) {
        this.date=date;
    }
    public void setDescription(String description) {
        this.description=description;
    }
    public void setIdWorker(int idWorker) {
        this.idWorker=idWorker;
    }
    public toString(){
        return "Reporte titulo: "+name+ ", descripcion: "+ description+", fecha: " +date+ ", Empleado: "+idWorker ;
    }
}