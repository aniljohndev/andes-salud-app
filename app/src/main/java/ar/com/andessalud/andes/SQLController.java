package ar.com.andessalud.andes;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {

    private SQLDBHelper dbhelper;
    private Context mContext;
    private SQLiteDatabase database;
    private Dialog dialog;

    public SQLController(Context contexto) {
        mContext = contexto;
    }

    public String registraHoraInicioAplicacion(String fechaActual) {
        String ultimaFecha="2014-06-18 12:56:50";
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='fechaInicioAplicacion'", null);
        String sql="";
        if (c.getCount()==0) {
            sql = "INSERT INTO PARAMETROS (parametro, val0 )" +
                    "Select 'fechaInicioAplicacion','" + fechaActual + "'";
            db.execSQL(sql);
        }else{
            c.moveToFirst();
            ultimaFecha=c.getString(0);
            sql = "update PARAMETROS set val0='" + fechaActual + "' " +
                    "where parametro='fechaInicioAplicacion'";
            db.execSQL(sql);
        }
        return ultimaFecha;
    }

    public Cursor leerEstadoRegistro() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT parametro as _id " +
                        ",val0 " +
                        "FROM PARAMETROS " +
                        "where parametro='estadoRegistro'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public void actualizarEstadoRegistro(String estadoRegistro) {
        String sql = "";
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='estadoRegistro'", null);

        if (c.getCount()==0) {
            sql = "INSERT INTO PARAMETROS (parametro, val0 )" +
                    "Select 'estadoRegistro','" + estadoRegistro + "'";
        }else {
            sql = "update PARAMETROS set val0='" + estadoRegistro + "' " +
                    "where parametro='estadoRegistro'";
        }
        db.execSQL(sql);
    };

    public void actualizarDatosIDAfiliado(String idAfiliado) {
        String sql = "";
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='idAfiliado'", null);

        if (c.getCount()==0) {
            sql = "INSERT INTO PARAMETROS (parametro, val0 )" +
                    "Select 'idAfiliado','" + idAfiliado + "'";
        }else {
            sql = "update PARAMETROS set val0='" + idAfiliado + "' " +
                    "where parametro='idAfiliado'";
        }
        db.execSQL(sql);
    }

    public String obtenerIDAfiliado() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";

        Cursor c = db.rawQuery("SELECT val0 _id " +
                "FROM PARAMETROS " +
                "where parametro='idAfiliado'",null);

        if (c != null) {
            c.moveToFirst();
            if (c.getCount()>0){
                return c.getString(0);
            }else{
                return "";
            }
        }
        db.close();
        return "";
    }

    public Cursor agregarAviso(String idGeneral) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("insert into CUENTA_AVISOS (_idGeneral) " +
                        "select  '"+idGeneral+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public String agregarOrdenConsultaAMB(String _idOrden,String _prestacionAutorizada,String _prestadorAutorizado
            ,String _domicilio1Autorizado,String _domicilio2Autorizado,String _codAutorizacion,String _fechaVencimiento
            ,String _nombreAfiliado, String fecEmision, String estado, String coseguro) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into ORDENESAMB (_idOrden, prestador,  _domicilio1Autorizado" +
                    ",_domicilio2Autorizado, _codAutorizacion, fecEmision, fecVencimiento, afiliado, estado, coseguro) " +
                    "select '" + _idOrden + "','" + _prestadorAutorizado + "','" + _domicilio1Autorizado
                    + "','" + _domicilio2Autorizado + "','" + _codAutorizacion + "','" + fecEmision
                    + "','"+_fechaVencimiento+ "','"+_nombreAfiliado+"','"+estado+"','"+coseguro+"'";
            db.execSQL(sql);

            sql = "insert into ORDENESAMBDetalle (_idOrden, prestacion,  cantidad, coseguro)"  +
                    "select '" + _idOrden + "','" + _prestacionAutorizada + "','1','" + coseguro+"'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public String verificaActTURNOS(String fecha) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='ultActTURNOS'", null);

        String sql="";
        if (c.getCount()==0) {
            sql = "insert into PARAMETROS(parametro, val0)"
                    + "select 'ultActTURNOS','" + fecha + "'";

            db.execSQL(sql);
            db.close();
            return "SI";
        }else{
            c.moveToFirst();
            if (c.getString(0).equals(fecha))
            {
                db.close();
                return "NO";
            }else{
                sql = "update PARAMETROS set val0='"+fecha+"' " +
                        "where parametro='ultActTURNOS'";
                db.execSQL(sql);
                db.close();
                return "SI";
            }
        }
    }

    public void eliminarBuzonTURNOS() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";
        sql = "delete from TURNOSANDES";
        db.execSQL(sql);
        sql = "delete from MENSAJES where contexto='TURNOS'";
        db.execSQL(sql);
    }

    public String verificaActORDENAMB(String fecha) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='ultActORDENAMB'", null);

        String sql="";
        if (c.getCount()==0) {
            sql = "insert into PARAMETROS(parametro, val0)"
                    + "select 'ultActORDENAMB','" + fecha + "'";

            db.execSQL(sql);
            db.close();
            return "SI";
        }else{
            c.moveToFirst();
            if (c.getString(0).equals(fecha))
            {
                db.close();
                return "NO";
            }else{
                sql = "update PARAMETROS set val0='"+fecha+"' " +
                        "where parametro='ultActORDENAMB'";
                db.execSQL(sql);
                db.close();
                return "SI";
            }
        }
    }

    public String verificaActCredProv(String fecha) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='ultActCREDPROV'", null);

        String sql="";
        if (c.getCount()==0) {
            sql = "insert into PARAMETROS(parametro, val0)"
                    + "select 'ultActCREDPROV','" + fecha + "'";

            db.execSQL(sql);
            db.close();
            return "SI";
        }else{
            c.moveToFirst();
            if (c.getString(0).equals(fecha))
            {
                db.close();
                return "NO";
            }else{
                sql = "update PARAMETROS set val0='"+fecha+"' " +
                        "where parametro='ultActCREDPROV'";
                db.execSQL(sql);
                db.close();
                return "SI";
            }
        }
    }

    public void eliminarBuzonCREDPROV() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";
        sql = "delete from CREDENCIALProvisoria";
        db.execSQL(sql);
    }

    public String agregarCREDPROV(String idAfiliado, String fecSolicitud, String tipoTarjeta
            ,String apellNomb, String numTarjeta, String fecVencimiento, String idCredencial) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into CREDENCIALProvisoria (idAfiliado,  fecSolicitud, tipoTarjeta" +
                    ", apellNomb, numTarjeta, fecVencimiento, idCredencial) " +
                    "select '" + idAfiliado + "','" + fecSolicitud + "','" + tipoTarjeta+ "','" + apellNomb
                    + "','" + numTarjeta + "','" + fecVencimiento + "','" + idCredencial+"'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public void eliminarNotificaciones() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";
        sql = "delete from MENSAJESAviso";
        db.execSQL(sql);
    }

    public Cursor agregarMensajeAvidoGen(String tipoContenido, String contenido, String idEnvio
            ,String asunto, String fecMsg) {
        String sql="";
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT idEnvio _id "+
                "FROM MENSAJESAviso " +
                "where idEnvio='"+idEnvio+"'", null);

        if (c.getCount()==0) {
            sql = "INSERT INTO MENSAJESAviso (idEnvio, tipoContenido, contenido, asunto, fecha )" +
                    "Select '" + idEnvio + "','" + tipoContenido + "','" + contenido +
                    "','" + asunto +"','" + fecMsg + "'";
            db.execSQL(sql);
        }

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleCREDPROV(String idCredencial) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idCredencial as _id " +
                        ",tipoTarjeta "+
                        ",fecSolicitud "+
                        ",apellNomb "+
                        ",numTarjeta "+
                        ",fecVencimiento "+
                        "FROM CREDENCIALProvisoria " +
                        "where idCredencial='"+idCredencial+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public void eliminarBuzonOrdenAMB() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";
        sql = "delete from ORDENESAMBDetalle";
        db.execSQL(sql);
        sql = "delete from ORDENESAMB";
        db.execSQL(sql);
        sql = "delete from MENSAJES where contexto='ORDENAMB'";
        db.execSQL(sql);
    }

    public String agregarORDENAMBEncabezado(String _idOrden,String _prestadorAutorizado, String _domicilio1Autorizado
            ,String _domicilio2Autorizado,String _codAutorizacion, String fecEmision, String _fechaVencimiento
            ,String _nombreAfiliado, String estado, String coseguro) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into ORDENESAMB (_idOrden, prestador,  _domicilio1Autorizado" +
                    ",_domicilio2Autorizado, _codAutorizacion, fecEmision, fecVencimiento, afiliado, estado, coseguro) " +
                    "select '" + _idOrden + "','" + _prestadorAutorizado + "','" + _domicilio1Autorizado
                    + "','" + _domicilio2Autorizado + "','" + _codAutorizacion + "','" + fecEmision
                    + "','"+_fechaVencimiento+ "','"+_nombreAfiliado+"','"+estado+"','"+coseguro+"'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public String agregarORDENAMBDetalle(String _idOrden,String nombrePrestacion, String cantidad, String coseguro) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into ORDENESAMBDetalle (_idOrden, prestacion,  cantidad, coseguro) " +
                    "select '" + _idOrden + "','" + nombrePrestacion + "','" + cantidad + "','" + coseguro + "'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public void agregarMensajeSincronizacion(String idMensaje, String idGeneral, String mensaje
            , String filaImagen, String desdeAPP, String fechaMsg, String contexto) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (mensaje.equals("---")){
            mensaje="";
        }

        if (filaImagen.equals("---")){
            filaImagen="";
        }

        String sql = "insert into mensajes(_idMensaje, idGeneral, mensaje, desdeAPP, fechaMsg, filaImagen, contexto)"
                + " select '" + idMensaje + "','" + idGeneral + "','" + mensaje + "','" + desdeAPP + "','"
                + fechaMsg+"','"+filaImagen+"','"+contexto+"'";

        db.execSQL(sql);
        db.close();
    }

    public Cursor leerBuzonAvisos(Context contexto) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select distinct * from (SELECT idOrden as _id " +
                        ", 'GUARDIAACTIVA' as codTipo"+
                        ", 'Guardia activa' as descTipo"+
                        ", fecEmision as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idOrden) as cantidadAvisos "+
                        "FROM GUARDIASACTIVAS " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'AMBAUT' as codTipo"+
                        ", 'Orden autorizada' as descTipo"+
                        ", fecEmision as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMB " +
                        "where estado='AUT' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'AMBAUD' as codTipo"+
                        ", 'Ambulatorio en proceso' as descTipo"+
                        ", fecEmision as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMB " +
                        "where estado='AUD' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'RECHAZADO' as codTipo"+
                        ", 'Ambulatorio no autorizado' as descTipo"+
                        ", fecEmision as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMB " +
                        "where estado='RECHAZADO' " +
                        "union all " +
                        "SELECT  _idOrden as _id " +
                        ", 'AMBOEAUD' as codTipo"+
                        ", 'Otras especialidades en proceso' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMBOtrasEsp " +
                        "where estado='AUD' " +
                        "union all " +
                        "SELECT distinct _idOrden as _id " +
                        ", 'ORDENAMBOTRASESPAUT' as codTipo"+
                        ", 'Otras especialidades autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMBOtrasEsp " +
                        "where estado='ORDENAMBOTRASESPAUT' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'RECORDENAMBOTRASESP' as codTipo"+
                        ", 'Otras especialidades no autorizado' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM ORDENESAMBOtrasEsp " +
                        "where estado='RECORDENAMBOTRASESP' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'PRACTAUD' as codTipo"+
                        ", 'Autorización en proceso' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM PRACTICAS " +
                        "where estado='AUD' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'PRACTAUT' as codTipo"+
                        ", 'Practica autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM PRACTICAS " +
                        "where estado='PRACTAUT' " +
                        "union all " +
                        "SELECT _idOrden as _id " +
                        ", 'RECHAZOPRACTICA' as codTipo"+
                        ", 'Práctica no autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idOrden) as cantidadAvisos "+
                        "FROM PRACTICAS " +
                        "where estado='RECHAZOPRACTICA' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'TURSINCONF' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='AUT' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'TURAUDIT' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='AUD' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'TURCANC' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='CANC' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'CONFTURANDES' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='ESPCONF' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'TURNCONF' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='TURNCONF' " +
                        "union all " +
                        "SELECT idTurno as _id " +
                        ", 'TURNRECH' as codTipo"+
                        ", 'Turno' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", nombreAfiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idTurno) as cantidadAvisos "+
                        "FROM TURNOSANDES " +
                        "where codEstado='RECHA' " +
                        "union all " +
                        "SELECT idIncGF as _id " +
                        ", 'INCGFAUD' as codTipo"+
                        ", 'Incorporación de GF en central' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", apellido || ', ' || nombre afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idIncGF) as cantidadAvisos "+
                        "FROM ingresoGF " +
                        "where codEstado='INCGFAUD' " +
                        "union all " +
                        "SELECT idIncGF as _id " +
                        ", 'INCGFAUT' as codTipo"+
                        ", 'Incorporación de GF autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", apellido || ', ' || nombre afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idIncGF) as cantidadAvisos "+
                        "FROM ingresoGF " +
                        "where codEstado='INCGFAUT' " +
                        "union all " +
                        "SELECT idIncGF as _id " +
                        ", 'INCGFREC' as codTipo"+
                        ", 'Incorporación de GF no autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", apellido || ', ' || nombre afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idIncGF) as cantidadAvisos "+
                        "FROM ingresoGF " +
                        "where codEstado='INCGFREC' " +
                        "union all " +
                        "SELECT idPresDoc as _id " +
                        ", 'PRESDOCAUD' as codTipo"+
                        ", 'Documentación enviada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idPresDoc) as cantidadAvisos "+
                        "FROM PRESENTACIONDOC " +
                        "where codEstado='PRESDOCAUD' " +
                        "union all " +
                        "SELECT idPresDoc as _id " +
                        ", 'PRESDOCREC' as codTipo"+
                        ", 'Documentación no autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idPresDoc) as cantidadAvisos "+
                        "FROM PRESENTACIONDOC " +
                        "where codEstado='PRESDOCREC' " +
                        "union all " +
                        "SELECT idPresDoc as _id " +
                        ", 'PRESDOCAUT' as codTipo"+
                        ", 'Documentación aceptada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idPresDoc) as cantidadAvisos "+
                        "FROM PRESENTACIONDOC " +
                        "where codEstado='PRESDOCAUT' " +
                        "union all " +
                        "SELECT _idInternacion as _id " +
                        ", 'INTSOL' as codTipo"+
                        ", 'Internación en proceso' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idInternacion) as cantidadAvisos "+
                        "FROM INTERNACIONES " +
                        "where codEstado='INTSOL' " +
                        "union all " +
                        "SELECT _idInternacion as _id " +
                        ", 'INFINTREC' as codTipo"+
                        ", 'Internación no autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idInternacion) as cantidadAvisos "+
                        "FROM INTERNACIONES " +
                        "where codEstado='INFINTREC' " +
                        "union all " +
                        "SELECT _idInternacion as _id " +
                        ", 'INFINTAUT' as codTipo"+
                        ", 'Internación autorizada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=_idInternacion) as cantidadAvisos "+
                        "FROM INTERNACIONES " +
                        "where codEstado='AUT' " +
                        "union all " +
                        "SELECT idCredencial as _id " +
                        ", 'SOLCRED' as codTipo"+
                        ", 'Solicitud de credencial' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idCredencial) as cantidadAvisos "+
                        "FROM CREDENCIAL " +
                        "where codEstado='SOLCRED' " +
                        "union all " +
                        "SELECT idCredencial as _id " +
                        ", 'IMPRESA' as codTipo"+
                        ", 'Impresión de credencial' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idCredencial) as cantidadAvisos "+
                        "FROM CREDENCIAL " +
                        "where codEstado='IMPRESA' " +
                        "union all " +
                        "SELECT idReclamo as _id " +
                        ", 'RECLAINI' as codTipo"+
                        ", 'Reclamo' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", '---' " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idReclamo) as cantidadAvisos "+
                        "FROM RECLAMOS " +
                        "where codEstado='INIRECLAMO' " +
                        "   and tipo='REC'" +
                        "union all " +
                        "SELECT idReclamo as _id " +
                        ", 'SUGINI' as codTipo"+
                        ", 'Sugerencia' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", '---' " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idReclamo) as cantidadAvisos "+
                        "FROM RECLAMOS " +
                        "where codEstado='SUGINI' " +
                        "   and tipo='SUG'" +
                        "union all " +
                        "SELECT idCredencial as _id " +
                        ", 'CREDPROV' as codTipo"+
                        ", 'Credencial provisoria' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", apellNomb as afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idCredencial) as cantidadAvisos "+
                        "FROM CREDENCIALProvisoria " +
                        "union all " +
                        "SELECT idCredencial as _id " +
                        ", 'CREDNUEVA' as codTipo"+
                        ", 'Credencial solicitada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", afiliado as afiliado " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idCredencial) as cantidadAvisos "+
                        "FROM CREDENCIALNueva " +
                        "union all " +
                        "SELECT idReclamo as _id " +
                        ", 'INIREC' as codTipo"+
                        ", 'Reclamo iniciado' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", '---' " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idReclamo) as cantidadAvisos "+
                        "FROM RECLAMOS " +
                        "where codEstado='INIREC' " +
                        "   and tipo='REC'" +
                        "union all " +
                        "SELECT idReclamo as _id " +
                        ", 'INISUG' as codTipo"+
                        ", 'Sugerencia iniciada' as descTipo"+
                        ", fecSolicitud as fecEmision" +
                        ", '---' " +
                        ", (select count(*)" +
                        "   from CUENTA_AVISOS " +
                        "   where _idGeneral=idReclamo) as cantidadAvisos "+
                        "FROM RECLAMOS " +
                        "where codEstado='INISUG' " +
                        "   and tipo='SUG'" +
                        "union all " +
                        "SELECT idEnvio as _id " +
                        ", 'NOTIFICACION' as codTipo"+
                        ", 'Notificación' as descTipo"+
                        ", fecha as fecEmision" +
                        ", asunto as afiliado " +
                        ", '---' " +
                        "FROM MENSAJESAviso "+
                        ") as todos "+
                        "order by substr(fecEmision, 7,4)" +
                        "   || substr(fecEmision, 4,2)" +
                        "   || substr(fecEmision, 1,2)" +
                        "   || substr(fecEmision, 12,2)" +
                        "   || substr(fecEmision, 15,2)" +
                        "   || substr(fecEmision, 18,2) desc"
                , null);
//
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor eliminarAviso(String idGeneral) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("delete from CUENTA_AVISOS " +
                        "where  _idGeneral='"+idGeneral+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor cuentaAvisos() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT count(*) _id " +
                        "FROM CUENTA_AVISOS "
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor eliminarTodosLosAvisos() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("delete from CUENTA_AVISOS"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleEstadoNotificacion(String idEnvio) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idEnvio as _id " +
                        ",tipoContenido "+
                        ",contenido "+
                        ",asunto "+
                        "FROM MENSAJESAviso " +
                        "where idEnvio='"+idEnvio+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public String verificaActORDENPRAC(String fecha) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT val0 _id "+
                "FROM PARAMETROS " +
                "where parametro='ultActORDENPRAC'", null);

        String sql="";
        if (c.getCount()==0) {
            sql = "insert into PARAMETROS(parametro, val0)"
                    + "select 'ultActORDENPRAC','" + fecha + "'";

            db.execSQL(sql);
            db.close();
            return "SI";
        }else{
            c.moveToFirst();
            if (c.getString(0).equals(fecha))
            {
                db.close();
                return "NO";
            }else{
                sql = "update PARAMETROS set val0='"+fecha+"' " +
                        "where parametro='ultActORDENPRAC'";
                db.execSQL(sql);
                db.close();
                return "SI";
            }
        }
    }

    public void eliminarBuzonOrdenPRAC() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="";
        sql = "delete from PRACTICASDetalle";
        db.execSQL(sql);
        sql = "delete from PRACTICAS";
        db.execSQL(sql);
        sql = "delete from MENSAJES where contexto='ORDENPRAC'";
        db.execSQL(sql);
    }

    public String agregarORDENPRACEncabezado(String _idOrden,String _idOrdenParcial, String _afiliado
            ,String _fecSolicitud,String _estado, String _fecFinalizacion, String _comentarioRechazo
            ,String _prestador, String _domicilio1Autorizado, String _coseguro, String _domicilio2Autorizado
            ,String _codAutorizacion, String _fechaVencimiento, String comentarioAfiliado) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into PRACTICAS (_idOrden, idOrdenParcial,  afiliado, fecSolicitud, estado" +
                    ",fecFinalizacion, comentariorechazo, prestador, _domicilio1Autorizado, coseguro, _domicilio2Autorizado"+
                    ",_codAutorizacion, _fechaVencimiento, comentarioAfiliado) " +
                    "select '" + _idOrden + "','" + _idOrdenParcial + "','" + _afiliado+ "','" + _fecSolicitud
                    + "','" + _estado + "','" + _fecFinalizacion + "','" + _comentarioRechazo+ "','" + _prestador
                    + "','"+_domicilio1Autorizado+ "','"+_coseguro+"','"+_domicilio2Autorizado+"','"+_codAutorizacion
                    + "','"+_fechaVencimiento+"','"+comentarioAfiliado+"'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public String agregarORDENPRACDetalle(String _idOrdenDetalle,String _idOrdenPrestacion, String _idOrdenParcial
            ,String _cantidad, String _prestacion, String _coseguroPrestacion) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into PRACTICASDetalle (idOrdenDetalle, _idOrden,  idOrdenParcial, cantidad, prestacion, coseguro) " +
                    "select '" + _idOrdenDetalle + "','" + _idOrdenPrestacion + "','" + _idOrdenParcial + "','" + _cantidad
                    +"','"+_prestacion+"','"+_coseguroPrestacion+"'";
            db.execSQL(sql);

            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public Cursor leerDetalleEstadoPracticasREC(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idOrden as _id " +
                        ",afiliado "+
                        ",fecSolicitud "+
                        ",fecFinalizacion "+
                        ",comentariorechazo "+
                        "FROM PRACTICAS " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public String agregarRechazoOrdenAMB(String idOrden, String mensajeRechazo, String fecMsg) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT _idOrden _id "+
                "FROM ORDENESAMB " +
                "where _idOrden='"+idOrden+"'", null);

        String sql="";
        if (c.getCount()==0) {
            db.close();
            return "NO";
        }else{
            sql = "update ORDENESAMB set _codAutorizacion='"+mensajeRechazo+"', fecVencimiento='"+fecMsg+"', estado='RECHAZADO' "+
                    "where _idOrden='"+idOrden+"'";
            db.execSQL(sql);
            db.close();
        }
        return "SI";
    }

    public String agregarAutorizacionOrdenAMB(String idOrden, String fecVenc, String fecMsg) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT _idOrden _id "+
                "FROM ORDENESAMB " +
                "where _idOrden='"+idOrden+"'", null);

        String sql="";
        if (c.getCount()==0) {
            db.close();
            return "NO";
        }else{
            sql = "update ORDENESAMB set fecVencimiento='"+fecVenc+"', estado='AUT' "+
                    "where _idOrden='"+idOrden+"'";
            db.execSQL(sql);
            db.close();
        }
        return "SI";
    }

    public Cursor leerDetalleEstadoAmbulatorio(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idOrden as _id " +
                        ",estado "+
                        "FROM ORDENESAMB " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleGuardiaActiva(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idOrden as _id " +
                        ",afiliado "+
                        ",prestador "+
                        ",prestacion "+
                        ",fecEmision "+
                        ",fecVencimiento "+
                        ",codigo "+
                        "FROM GUARDIASACTIVAS " +
                        "where idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleAmbulatorioAUT(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idOrden as _id " +
                        ", prestador "+
                        ", _domicilio1Autorizado "+
                        ", _domicilio2Autorizado "+
                        ", _codAutorizacion "+
                        ", fecEmision "+
                        ", fecVencimiento "+
                        ", afiliado "+
                        ", coseguro "+
                        "FROM ORDENESAMB " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleAmbulatorioPrestacionesAUT(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idOrden as _id " +
                        ", cantidad "+
                        ", prestacion "+
                        ", coseguro "+
                        "FROM ORDENESAMBDetalle " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public void agregarGuardiActiva(String idOrden, String prestador, String prestacion
            , String afiliado, String fecEmision, String fecVencimiento, String codigo, String visible) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into GUARDIASACTIVAS(idOrden, prestador, prestacion, afiliado, fecEmision, fecVencimiento, codigo, visible)"
                + "select '" + idOrden + "','" + prestador + "','" + prestacion + "','" + afiliado + "','"
                + fecEmision + "','" + fecVencimiento+"','"+codigo+"','"+visible+"'";

        db.execSQL(sql);
        db.close();
    }

    public void eliminarZonasEmergencias(Context contexto) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "delete from EMERGENCIAS_ZONAS";
        db.execSQL(sql);
        db.close();
    }

    public void agregarZonasEmergencias(Context contexto, String idZonaEMZona, String nombreZona) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into EMERGENCIAS_ZONAS (_idZona, nombreZona) " +
                "select '"+idZonaEMZona+"','"+nombreZona+"'";

        db.execSQL(sql);
        db.close();
    }

    public void actualizarEmergencia(Context contexto, String idEmergencia, String nombreEmergencia) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idEmergencia _id FROM EMERGENCIAS where _idEmergencia='"+idEmergencia+"'", null);
        if (c.getCount()>0){
            String sql = "update EMERGENCIAS set nombreEmergencia='"+nombreEmergencia+"' "+
                    "where _idEmergencia='"+idEmergencia+"'";

            db.execSQL(sql);
        }
        else {
            String sql = "insert into EMERGENCIAS (_idEmergencia, nombreEmergencia) " +
                    "select '" + idEmergencia + "','" + nombreEmergencia + "'";

            db.execSQL(sql);
        }
        db.close();
    }

    public void eliminarEmergenciaDeZonas(Context contexto) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "delete from REL_EMERGENCIAS_ZONAS";
        db.execSQL(sql);
        db.close();
    }

    public void agregarEmergenciaAZona(Context contexto, String _idEmergencia, String _idZona) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into REL_EMERGENCIAS_ZONAS (_idEmergencia, _idZona) " +
                "select '"+_idEmergencia+"','"+_idZona+"'";

        db.execSQL(sql);
        db.close();
    }

    public void eliminarTelefonosEmergencia(Context contexto, String _idZona) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "delete from TELEFONOS_EMERGENCIA where idZona='"+_idZona+"'";
        db.execSQL(sql);
        db.close();
    }

    public void agregarTelefonoEmergencia(Context contexto, String idEmergencia,String idZona, String telefono) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into TELEFONOS_EMERGENCIA (_idEmergencia, idZona, telefono) " +
                "select '"+idEmergencia+"','"+idZona+"','"+telefono+"'";
        db.execSQL(sql);
        db.close();
    }

    public void actualizarParametro(Context contexto, String parametro, String valor) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "delete from PARAMETROS where parametro='"+parametro+"'";
        db.execSQL(sql);

        sql = "insert into PARAMETROS (parametro,val0) "+
                "select '"+parametro+"','"+valor+"'";
        db.execSQL(sql);
        db.close();
    }

    public Cursor leerZonasEmergencias(Context contexto) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idZona _id, nombreZona FROM EMERGENCIAS_ZONAS " +
                "order by nombreZona", null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerEmergenciasPorZona(Context contexto, String _idZona) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT EMERGENCIAS._idEmergencia as _id " +
                        ",nombreEmergencia "+
                        ",REL_EMERGENCIAS_ZONAS._idZona "+
                        "FROM REL_EMERGENCIAS_ZONAS " +
                        "   INNER JOIN EMERGENCIAS  on REL_EMERGENCIAS_ZONAS._idEmergencia=EMERGENCIAS._idEmergencia " +
                        "where REL_EMERGENCIAS_ZONAS._idZona='"+_idZona+"' " +
                        "order by EMERGENCIAS.nombreEmergencia"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerTelefonosEmergencia(Context contexto, String _idEmergencia, String _idZona) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT TELEFONOS_EMERGENCIA._idEmergencia as _id " +
                        ",telefono " +
                        "FROM TELEFONOS_EMERGENCIA " +
                        "where TELEFONOS_EMERGENCIA._idEmergencia='"+_idEmergencia+"' and TELEFONOS_EMERGENCIA.idZona='"+_idZona+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public String agregarOrdenPractica(String idOrden,String fecSolicitud,String nombreAfiliado) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into PRACTICAS (_idOrden, afiliado, fecSolicitud, estado) " +
                    "select '" + idOrden + "','" + nombreAfiliado + "','" + fecSolicitud + "','AUD'";

            db.execSQL(sql);
            db.close();
            return "";

        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public void agregarCredencialProvisoria(String idAfiliado, String fecSolicitud, String tipoTarjeta, String apellNomb
            , String numTarjeta, String fecVencimiento, String idCredencial) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "delete from CREDENCIALProvisoria where idAfiliado='"+idAfiliado+"'";
        db.execSQL(sql);

        sql = "insert into CREDENCIALProvisoria(idAfiliado, fecSolicitud, tipoTarjeta, apellNomb, numTarjeta, fecVencimiento, idCredencial)"
                + "select '" + idAfiliado + "','" + fecSolicitud + "','"+tipoTarjeta+"','"+apellNomb+"','"+numTarjeta+"','"
                +fecVencimiento+"','"+idCredencial+"'";

        db.execSQL(sql);
        db.close();
    }

    public void agregarInternacionSolicitud(String idInternacion, String fecSolicitud, String afiliado) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into internaciones(_idInternacion, fecSolicitud, codEstado, afiliado, fecFinalizacion, coseguro, comentarioRechazo)"
                + "select '" + idInternacion + "','" + fecSolicitud + "','INTSOL','" + afiliado + "','','',''";

        db.execSQL(sql);
        db.close();
    }

    public Cursor leerTipoDocumentacion() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idTipoDoc as _id " +
                        ",tipoDoc "+
                        ",0 as orden "+
                        "FROM TIPODOCUMENTACION " +
                        "union all "+
                        "SELECT '99' as _id " +
                        ",'OTROS' as tipoDoc "+
                        ",1 as orden "+
                        "order by orden, tipoDoc"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public void agregarPresentacionDoc(String idIncGF, String fecSolicitud, String afiliado
            ,String tipoDoc) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into PRESENTACIONDOC(idPresDoc, fecSolicitud, afiliado, tipoDoc, codEstado) "
                + "select '" + idIncGF + "','" + fecSolicitud + "','" + afiliado + "','"+ tipoDoc +"','PRESDOCAUD'";

        db.execSQL(sql);
        db.close();
    }

    public Cursor leerParentescos() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idParentesco as _id " +
                        ",parentesco "+
                        ",0 as orden "+
                        "FROM PARENTESCOS " +
                        "where _idParentesco<>'9999' "+
                        "union all "+
                        "SELECT _idParentesco as _id " +
                        ",parentesco "+
                        ",1 as orden "+
                        "FROM PARENTESCOS " +
                        "where _idParentesco='9999' "+
                        "order by orden, parentesco"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerNacionalidad() {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idNacionalidad as _id " +
                        ",nacionalidad "+
                        ",0 as orden "+
                        "FROM NACIONALIDAD " +
                        "where _idNacionalidad<>'9999' "+
                        "union all "+
                        "SELECT _idNacionalidad as _id " +
                        ",nacionalidad "+
                        ",1 as orden "+
                        "FROM NACIONALIDAD " +
                        "where _idNacionalidad='9999' "+
                        "order by orden, nacionalidad"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public void agregarIngresoGF(String idIncGF, String fecSolicitud, String idEstado
            ,String CUIL, String _datoApellido, String _datoNombre, String _datoParentesco, String _datoSexo, String _datoFecNac
            ,String comentario) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "insert into ingresoGF(idIncGF, fecSolicitud, _idEstado, cuil, apellido, nombre, parentesco, sexo" +
                ", fecNac, comentario, codEstado) "
                + "select '" + idIncGF + "','" + fecSolicitud + "','" + idEstado + "','"+ CUIL +"','" + _datoApellido + "','"
                + _datoNombre + "','" + _datoParentesco+"','"+_datoSexo+"','"+_datoFecNac+"','"+comentario+"','INCGFAUD'";

        db.execSQL(sql);
        db.close();
    }

    public String agregarTurnoAndes(String _idTurno, String _nombreAfiliado, String _nombreEspecialidad
            , String _nombreCentro, String _direccionCentro, String _nombreMedico, String _fechaTurno, String fecSolicitud
            , String codEstado, String coseguro) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String sql = "insert into TURNOSANDES(idTurno, nombreAfiliado, nombreEspecialidad, nombreCentro, direccionCentro, nombreMedico, fechaTurno" +
                    ", fecSolicitud, codEstado, coseguro)"
                    + "select '" + _idTurno + "','" + _nombreAfiliado + "','" + _nombreEspecialidad + "','" + _nombreCentro + "'" +
                    ",'" + _direccionCentro + "','" + _nombreMedico + "','" + _fechaTurno + "','" + fecSolicitud + "','" + codEstado + "','" + coseguro + "'";

            db.execSQL(sql);
            db.close();
            return "";
        }catch (Exception e){
            db.close();
            return e.getMessage();
        }
    }

    public Cursor leerDetalleTURANDES(String idTurno) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idTurno as _id " +
                        ",fechaTurno "+
                        ",nombreMedico "+
                        ",nombreCentro "+
                        ",direccionCentro "+
                        ",coseguro "+
                        "FROM TURNOSANDES " +
                        "where idTurno='"+idTurno+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public String cancelarTurnoANDES(String idTurno) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT idTurno _id "+
                "FROM TURNOSANDES " +
                "where idTurno='"+idTurno+"'", null);

        String sql="";
        if (c.getCount()==0) {
            return "NO";
        }else{
            sql = "update TURNOSANDES set codEstado='CANC' " +
                    "where idTurno='"+idTurno+"'";
            db.execSQL(sql);
        }
        return "SI";
    }

    public Cursor agregarMensajeAvisoGen(String tipoContenido, String contenido, String idEnvio
            ,String asunto, String fecMsg) {
        String sql="";
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT idEnvio _id "+
                "FROM MENSAJESAviso " +
                "where idEnvio='"+idEnvio+"'", null);

        if (c.getCount()==0) {
            sql = "INSERT INTO MENSAJESAviso (idEnvio, tipoContenido, contenido, asunto, fecha )" +
                    "Select '" + idEnvio + "','" + tipoContenido + "','" + contenido +
                    "','" + asunto +"','" + fecMsg + "'";
            db.execSQL(sql);
        }

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerOrdenesPracticasAUT(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idOrdenParcial as _id " +
                        ",prestador "+
                        ",_codAutorizacion "+
                        "FROM PRACTICAS " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleOrdenPracticas(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idOrdenParcial as _id " +
                        ",prestador "+
                        ",_codAutorizacion "+
                        ",fecSolicitud "+
                        ",_fechaVencimiento "+
                        ",_domicilio1Autorizado "+
                        ",_domicilio2Autorizado "+
                        ",comentarioAfiliado "+
                        ",coseguro "+
                        "FROM PRACTICAS " +
                        "where idOrdenParcial='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerPracticasEnOrden(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT idOrdenParcial as _id " +
                        ",cantidad "+
                        ",prestacion "+
                        ",coseguro "+
                        "FROM PRACTICASDetalle " +
                        "where idOrdenParcial='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor leerDetalleAmbulatorioREC(String idOrden) {
        SQLDBHelper dbHelper = new SQLDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT _idOrden as _id " +
                        ",afiliado "+
                        ",fecEmision "+
                        ",_codAutorizacion "+
                        ",fecVencimiento "+
                        "FROM ORDENESAMB " +
                        "where _idOrden='"+idOrden+"'"
                , null);

        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }
}
