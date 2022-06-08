package ar.com.andessalud.andes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Usuario on 20/08/2016.
 */
public class SQLDBHelper extends SQLiteOpenHelper {

    Context mContext;

    public SQLDBHelper(Context context){
        //Creamos la BD
        super(context, "andesDB", null, 12);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS PARAMETROS(parametro TEXT ,val0 TEXT, val1 TEXT, val2 TEXT, val3 TEXT, val4 TEXT, val5 TEXT" +
                ", val6 TEXT, val7 TEXT, val8 TEXT, val9 TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ACT_PENDIENTES( _idPendiente TEXT PRIMARY KEY, tipoPendiente TEXT, idGeneral TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS GUARDIASACTIVAS(idOrden TEXT, prestador TEXT, prestacion TEXT, afiliado TEXT, fecEmision TEXT" +
                ", fecVencimiento TEXT, codigo TEXT, visible TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS EMERGENCIAS(_idEmergencia TEXT PRIMARY KEY, nombreEmergencia TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS REL_EMERGENCIAS_ZONAS(_idEmergencia TEXT, _idZona TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS EMERGENCIAS_ZONAS(_idZona TEXT PRIMARY KEY, nombreZona TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TELEFONOS_EMERGENCIA(_idEmergencia TEXT , idZona TEXT, telefono TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ORDENESAMB(_idOrden TEXT PRIMARY KEY, prestador TEXT, _domicilio1Autorizado TEXT" +
                ", _domicilio2Autorizado TEXT, _codAutorizacion TEXT, fecEmision TEXT, fecVencimiento TEXT" +
                ", afiliado TEXT, estado TEXT, coseguro TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ORDENESAMBDetalle(_idOrden TEXT, prestacion TEXT, cantidad TEXT, coseguro TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ORDENESAMBOtrasEsp(_idOrden TEXT, idOrdenParcial TEXT, afiliado TEXT, fecSolicitud TEXT" +
                ", estado TEXT, fecFinalizacion TEXT, comentariorechazo TEXT, prestador TEXT, _domicilio1Autorizado TEXT, coseguro TEXT" +
                ", _domicilio2Autorizado TEXT, _codAutorizacion TEXT, _fechaVencimiento TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ORDENESAMBDetalleOtrasEsp(idOrdenDetalle TEXT PRIMARY KEY,_idOrden TEXT, idOrdenParcial TEXT" +
                ", cantidad TEXT, prestacion TEXT, coseguro TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS PRACTICAS(_idOrden TEXT, idOrdenParcial TEXT, afiliado TEXT, fecSolicitud TEXT" +
                ", estado TEXT, fecFinalizacion TEXT, comentariorechazo TEXT, prestador TEXT, _domicilio1Autorizado TEXT, coseguro TEXT" +
                ", _domicilio2Autorizado TEXT, _codAutorizacion TEXT, _fechaVencimiento TEXT, comentarioAfiliado TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PRACTICASDetalle(idOrdenDetalle TEXT PRIMARY KEY,_idOrden TEXT, idOrdenParcial TEXT" +
                ", cantidad TEXT, prestacion TEXT, coseguro TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS TURNOS(_idTurno TEXT PRIMARY KEY, afiliado TEXT, fechaTurno TEXT, fecSolicitud TEXT" +
                ", profesional TEXT, policonsultorio TEXT, direccion TEXT , especialidad TEXT, estado TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ingresoGF(idIncGF TEXT PRIMARY KEY, fecSolicitud TEXT" +
                ", _idEstado TEXT, apellido TEXT, nombre TEXT, parentesco TEXT, cuil TEXT, sexo TEXT, fecNac TEXT" +
                ", comentario TEXT, codEstado TEXT " +
                ", _idComentarioRechazo TEXT, comentarioRechazo TEXT, fechaRechazo TEXT" +
                ", _idComentarioIncorporado TEXT, comentarioIncorporado TEXT, fechaIncorporado TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS PRESENTACIONDOC(idPresDoc TEXT PRIMARY KEY, fecSolicitud TEXT" +
                ", afiliado TEXT, tipoDoc TEXT, codEstado TEXT, comentario TEXT, fecha TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS MENSAJES( _idMensaje TEXT PRIMARY KEY, idGeneral TEXT NOT NULL, mensaje TEXT" +
                ", filaImagen TEXT,desdeAPP TEXT, fechaMsg TEXT, contexto TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS INTERNACIONES( _idInternacion TEXT PRIMARY KEY, fecSolicitud TEXT NOT NULL, codEstado TEXT" +
                ",afiliado TEXT, fecFinalizacion TEXT, comentarioRechazo TEXT, coseguro TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS INTERNACIONESDetalle( _idInternacion TEXT, idInternacionDetalle TEXT, prestador TEXT" +
                ",palabraClave TEXT, cantidad TEXT, prestacion TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS CUENTA_AVISOS( _idGeneral TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIAL( idCredencial TEXT, fecSolicitud TEXT, afiliado TEXT, codEstado TEXT" +
                ", fecFinalizacion TEXT, idAfiliado TEXT, nroCRM TEXT, nombreDelegacion TEXT, domDelegacion TEXT, fecEstimadaLista TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALEVENTO( idCredencial TEXT, fecEvento TEXT, mensaje TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS RECLAMOS(idReclamo TEXT, fecSolicitud TEXT, codEstado TEXT, nroCRM TEXT" +
                ", tipo TEXT, fecFinalizacion TEXT, comentariofinalizacion TEXT)");

        //region parametros de familiares
        db.execSQL("CREATE TABLE IF NOT EXISTS PARENTESCOS(_idParentesco TEXT PRIMARY KEY, parentesco TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ESTADOCIVIL(_idEstadoCivil TEXT PRIMARY KEY, estadoCivil TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS NACIONALIDAD(_idNacionalidad TEXT PRIMARY KEY, nacionalidad TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS SEXO(_idSexo TEXT PRIMARY KEY, sexo TEXT)");

        db.execSQL("INSERT INTO PARENTESCOS(_idParentesco, parentesco) SELECT '1','Cónyuge'");
        db.execSQL("INSERT INTO PARENTESCOS(_idParentesco, parentesco) SELECT '2','Concubino/a'");
        db.execSQL("INSERT INTO PARENTESCOS(_idParentesco, parentesco) SELECT '3','Hijo/a'");
        db.execSQL("INSERT INTO PARENTESCOS(_idParentesco, parentesco) SELECT '4','Hijo/a del cónyuge o concubino/a'");
        db.execSQL("INSERT INTO PARENTESCOS(_idParentesco, parentesco) SELECT '5','Nieto/a'");

        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '1','Casado/a'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '2','Concubinato'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '3','Convivencia'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '4','Cónyuge'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '5','Divorciado/a'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '6','Separado/a'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '7','Soltero/a'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '8','Viudo/a'");
        db.execSQL("INSERT INTO ESTADOCIVIL(_idEstadoCivil, estadoCivil) SELECT '9999','Otro'");

        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '1','Argentina'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '2','Chilena'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '3','Peruana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '4','Uruguaya'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '5','Brasilera'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '6','Colombiana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '7','Mexicana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '8','Boliviana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '9','Cubana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '10','Venezolana'");
        db.execSQL("INSERT INTO NACIONALIDAD(_idNacionalidad, nacionalidad) SELECT '9999','Otro'");

        db.execSQL("INSERT INTO SEXO(_idSexo, sexo) SELECT '1','Masculino'");
        db.execSQL("INSERT INTO SEXO(_idSexo, sexo) SELECT '2','Femenino'");
        //endregion

        //region tipos de documentacion
        db.execSQL("CREATE TABLE IF NOT EXISTS TIPODOCUMENTACION(idTipoDoc TEXT PRIMARY KEY, tipoDoc TEXT)");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '10','Acta de matrimonio/concubinato'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '15','Alta temprana AFIP'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '20','Bono de sueldo'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '25','Certificado de estudios'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '30','Constancia de CUIL'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '35','DNI'");
        db.execSQL("INSERT INTO TIPODOCUMENTACION(idTipoDoc, tipoDoc) SELECT '40','Partida de nacimiento'");
        //enedregion

        //retion turNet
        db.execSQL("CREATE TABLE IF NOT EXISTS TURNOSTURNET(idTurno TEXT, fecSolicitud TEXT, nombreAfiliado TEXT, nombreEspecialidad TEXT" +
                ", nombreCentro TEXT, direccionCentro TEXT, fechaTurno TEXT, horaDesde TEXT, codEstado TEXT)");
        //endregion

        db.execSQL("CREATE TABLE IF NOT EXISTS PREFERIDOS(idElemento TEXT, tipoPrestador TEXT, tipoAccion TEXT, prestador TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PREFERIDOSDom(idElemento TEXT, idDomicilio TEXT, linea1 TEXT, linea2 TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PREFERIDOSDomTel(idElemento TEXT, idDomicilio TEXT, telefono TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALProvisoria(idAfiliado TEXT, fecSolicitud TEXT, tipoTarjeta TEXT, apellNomb TEXT, numTarjeta TEXT, fecVencimiento TEXT, idCredencial TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS TURNOSANDES(idTurno TEXT, nombreAfiliado  TEXT, nombreEspecialidad  TEXT, nombreCentro  TEXT" +
                ", direccionCentro  TEXT, nombreMedico  TEXT, fechaTurno  TEXT, fecSolicitud  TEXT, codEstado  TEXT, coseguro  TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS MENSAJES_CHAT(mensaje TEXT,desdeAPP TEXT, fechaMsg TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALNueva(idCredencial TEXT, afiliado TEXT, fecSolicitud TEXT, estado TEXT, sedeDestino TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALNuevaDetalle(idCredencialDetalle TEXT, frase TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS MENSAJESAviso(idEnvio TEXT, tipoContenido TEXT, contenido TEXT, fecha TEXT, asunto TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS CREDENCIALNueva");
        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALNueva(idCredencial TEXT, afiliado TEXT, fecSolicitud TEXT, estado TEXT, sedeDestino TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CREDENCIALNuevaDetalle(idCredencialDetalle TEXT, frase TEXT)");
        db.execSQL("DROP TABLE IF EXISTS MENSAJESAviso");
        db.execSQL("CREATE TABLE IF NOT EXISTS MENSAJESAviso(idEnvio TEXT, tipoContenido TEXT, contenido TEXT, fecha TEXT, asunto TEXT)");
    }
}

