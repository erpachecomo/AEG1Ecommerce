package mx.edu.ittepic.ejbs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import mx.edu.ittepic.entities.Category;
import mx.edu.ittepic.entities.Product;
import mx.edu.ittepic.entities.Role;
import mx.edu.ittepic.entities.Users;
import mx.edu.ittepic.utils.Message;

@Stateless
public class Conect
{

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    EntityManager entity;

//-----------------------------------------------Roles------------------------------------------------------------------------
    //Consultar
    public String getRoles()
    {
        List<Role> listRoles;
        Message m = new Message();

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.create();
        Query q = entity.createNamedQuery( "Role.findAll" );
        listRoles = q.getResultList();
        // 14-10 ms
        String roles = "[";
        for ( Role listRole : listRoles )
        {
            roles = roles + "{\"roleid\":" + listRole.getRoleid() + ",\"rolename\":\"" + listRole.getRolename() + "\"},";
        }
        //   29ms
        //for(int i=0;i<listRoles.size();i++)
        //  listRoles.get(i).setUsersList(null);
        roles = roles.substring( 0 , roles.length() - 1 );
        roles = roles + "]";
        //  222 ms
        m.setCode( 200 );
        m.setMsg( roles );
        m.setDetails( "OK" );
        return gson.toJson( m );
        //return listRoles.toString();
    }

    public String getRole( String roleid )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Role role;
        Query q = entity.createNamedQuery( "Role.findByRoleid" ).
            setParameter( "roleid" , Integer.parseInt( roleid ) );

        role = ( Role ) q.getSingleResult();
        role.setUsersList( null );
        m.setCode( 200 );
        m.setMsg( gson.toJson( role ) );
        m.setDetails( "OK" );
        return gson.toJson( m );
    }

    public String getRoleByName( String rolename )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Role role;
        try
        {

            Query q = entity.createQuery( "select o from Role o "
                + "where o.rolename like :rolename" );
            q.setParameter( "rolename" , rolename );
            role = ( Role ) q.getSingleResult();
            role.setUsersList( null );

            m.setCode( 200 );
            m.setMsg( gson.toJson( role ) );
            m.setDetails( "OK" );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error Ilegal de tipo de dato" );
            m.setDetails( "Advertencia" );

        }
        catch ( NoResultException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro registro con ese nombre" );
            m.setDetails( "Advertencia" );

        }
        return gson.toJson( m );

    }

    /*
    public String getRoleByName(String rolename){
        Message m= new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson= builder.create();
        Role role;
        Query q= entity.createNamedQuery("Role.findByRolename").
        setParameter("rolename", rolename);
        role = (Role) q.getSingleResult();
        role.setUsersList(null);
        m.setCode(200);
        m.setMsg(gson.toJson(role));
        m.setDetails("OK");
        return gson.toJson(m);
    }*//*
     public String getRole(String rolen){
        List<Role> listRoles;
        Message m= new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson= builder.create();
        Query q= entity.createNamedQuery("Role.findByRolename");
        q.setParameter("rolename", rolen);
        return q.getResultList().get(0).toString();
    }*/

    public String updateRole( String roleid , String name )
    {
        Message m = new Message();
        Role r = new Role();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //Query JPA
        /*  Query q=entity.createNamedQuery("Role.updateRole")
                .setParameter("name", name).
                setParameter("id",Integer.parseInt(roleid));*/
        //Query Nativo
        /*Query q=entity.createNativeQuery("UPDATE Role SET rolename='"+name+"' WHERE roleid="+roleid);

        if(q.executeUpdate()==1){
            m.setCode(200);
            m.setMsg("Se actualizo correctamente");
            m.setDetails("OK");
        }else{
            m.setCode(200);
            m.setMsg("No se realizo la actualizacion");
            m.setDetails("NO SE ENCONTRO EL REGISTRO A ACTUALIZAR");
        }
         */
        //Actualizar por medio de find y merge
        try
        {
            //busca el registro establecido
            r = entity.find( Role.class , Integer.parseInt( roleid ) );
            //si no encuentra nada no se actualiza
            if ( r == null )
            {
                m.setCode( 200 );
                m.setMsg( "el role NO se actualizó correctamente" );
                m.setDetails( "No se encontro el Registro" );
            }
            else
            {//si lo encuentra lo actualiza
                r.setRolename( name );
                entity.merge( r );
                m.setCode( 200 );
                m.setMsg( "el role se actualizó correctamente" );
                m.setDetails( "OK" );
            }
        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato '" + roleid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad '" + roleid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad '" + roleid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( EntityNotFoundException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro el roleid para actualizar '" + roleid + "'" );
            m.setDetails( e.toString() );
        }
        return gson.toJson( m );
    }

    public String newRole( String name )
    {
        Message m = new Message();
        Role r = new Role();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try
        {
            //r.setRoleid(Integer.parseInt("0"));
            r.setRolename( name );
            //Gurdar en la BD (Actualiza)
            //entity.merge(r);
            //GlassFish Guarda en la BD
            entity.persist( r );
            //obliga que GlassFish guarde en ese momento
            entity.flush();
            m.setCode( 200 );
            m.setMsg( "el role se inserto correctamente y su roleid es " );
            m.setDetails( r.getRoleid().toString() );
        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad " );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad " );
            m.setDetails( e.toString() );
        }
        return gson.toJson( m );
    }

    public String DeleteRol( String roleid )
    {
        Message m = new Message();
        Role role;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try
        {
            //Maneja Exepcion si no encuentra roloid
            Query q = entity.createNamedQuery( "Role.findByRoleid" ).
                setParameter( "roleid" , Integer.parseInt( roleid ) );
            //se asigna el reultado a role
            role = ( Role ) q.getSingleResult();
            role.setUsersList( null );
            //esta funcion busca por id en la clase
            //role = entity.find(Role.class,Integer.parseInt(roleid));
            entity.remove( role );
            m.setCode( 200 );
            m.setMsg( "el role se elimino correctamente" );
            m.setDetails( "OK" );
        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad " );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad " );
            m.setDetails( e.toString() );
        }
        return gson.toJson( m );
    }

//------------------------------------------Product------------------------------------------------------------------
    public String newProduct( String code , String productname , String brand ,
        String purchprice , String stock , String salepricemin ,
        String reorderpoint , String currency , String salepricemay , String categoryid )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try
        {
            Product p = new Product();
            Category cat = entity.find( Category.class , Integer.parseInt( categoryid ) );

            p.setCode( code );
            p.setCurrency( currency );
            p.setProductname( productname );
            p.setBrand( brand );
            p.setCategoryid( cat );
            p.setPurchprice( Double.parseDouble( purchprice ) );
            p.setReorderpoint( Integer.parseInt( reorderpoint ) );
            p.setSalepricemay( Double.parseDouble( salepricemay ) );
            p.setSalepricemin( Double.parseDouble( salepricemin ) );
            p.setStock( Integer.parseInt( stock ) );

            entity.persist( p );
            entity.flush();

            m.setCode( 200 );
            m.setMsg( "El registro se inserto correctamente" );
            m.setDetails( p.getProductid().toString() );

        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad " );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad " );
            m.setDetails( e.toString() );
        }
        return gson.toJson( m );
    }

    public String getProducts()
    {
        List<Role> listPro;
        Message m = new Message();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try
        {
            Query q = entity.createNamedQuery( "Product.findAll" );
            listPro = q.getResultList();

            m.setCode( 200 );
            m.setMsg( gson.toJson( listPro ) );
            m.setDetails( "OK" );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error Ilegal de tipo de dato" );
            m.setDetails( "Advertencia" );

        }
        catch ( NoResultException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro registro con ese nombre" );
            m.setDetails( "Advertencia" );

        }
        return gson.toJson( m );

    }

    public String getProductsById( String productid )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Product p;
        try
        {
            Query q = entity.createNamedQuery( "Product.findByProductid" ).
                setParameter( "productid" , Integer.parseInt( productid ) );

            p = ( Product ) q.getSingleResult();
            p.setSaleslineList( null );

            m.setCode( 200 );
            m.setMsg( gson.toJson( p ) );
            m.setDetails( "OK" );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error Ilegal de tipo de dato" );
            m.setDetails( "Advertencia" );

        }
        catch ( NoResultException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro registro con ese nombre" );
            m.setDetails( "Advertencia" );

        }
        return gson.toJson( m );
    }

    public String getProductsByName( String productname )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Product p;
        try
        {
            Query q = entity.createNamedQuery( "Product.findByProductname" ).
                setParameter( "productname" , productname );

            p = ( Product ) q.getSingleResult();
            p.setSaleslineList( null );

            m.setCode( 200 );
            m.setMsg( gson.toJson( p ) );
            m.setDetails( "OK" );

        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error Ilegal de tipo de dato" );
            m.setDetails( "Advertencia" );

        }
        catch ( NoResultException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro registro con ese nombre" );
            m.setDetails( "Advertencia" );

        }
        return gson.toJson( m );

    }

    public String DeletePro( String productid )
    {
        Message m = new Message();
        Product p;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try
        {
            //Maneja Exepcion si no encuentra roloid
            Query q = entity.createNamedQuery( "Product.findByProductid" ).
                setParameter( "productid" , Integer.parseInt( productid ) );
            //se asigna el reultado a role
            p = ( Product ) q.getSingleResult();
            p.setSaleslineList( null );
            //esta funcion busca por id en la clase
            //role = entity.find(Role.class,Integer.parseInt(roleid));
            entity.remove( p );
            m.setCode( 200 );
            m.setMsg( "el Producto se elimino correctamente" );
            m.setDetails( "OK" );
        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad " );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad " );
            m.setDetails( e.toString() );
        }
        return gson.toJson( m );
    }

    public String UpdateProduct( String productid , String code , String productname , String brand ,
        String purchprice , String stock , String salepricemin ,
        String reorderpoint , String currency , String salepricemay , String categoryid )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Product p = new Product();
        try
        {
            p = entity.find( Product.class , Integer.parseInt( productid ) );
            if ( p == null )
            {
                m.setCode( 200 );
                m.setMsg( "el Producto NO se actualizó correctamente" );
                m.setDetails( "No se encontro el Registro" );
            }
            else
            {//si lo encuentra lo actualiza
                Category cat = entity.find( Category.class , Integer.parseInt( categoryid ) );
                p.setCode( code );
                p.setCurrency( currency );
                p.setProductname( productname );
                p.setBrand( brand );
                p.setCategoryid( cat );
                p.setPurchprice( Double.parseDouble( purchprice ) );
                p.setReorderpoint( Integer.parseInt( reorderpoint ) );
                p.setSalepricemay( Double.parseDouble( salepricemay ) );
                p.setSalepricemin( Double.parseDouble( salepricemin ) );
                p.setStock( Integer.parseInt( stock ) );

                entity.merge( p );
                m.setCode( 200 );
                m.setMsg( "el Producto se actualizó correctamente" );
                m.setDetails( "OK" );
            }
        }
        catch ( NumberFormatException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error de tipo de dato '" + productid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 422 );
            m.setMsg( "Su instancia no es una entidad '" + productid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( TransactionRequiredException e )
        {
            m.setCode( 500 );
            m.setMsg( "no hay ninguna transacción para invocar gestor de la entidad '" + productid + "'" );
            m.setDetails( e.toString() );
        }
        catch ( EntityNotFoundException e )
        {
            m.setCode( 406 );
            m.setMsg( "No se encontro el roleid para actualizar '" + productid + "'" );
            m.setDetails( e.toString() );
        }

        return gson.toJson( m );

    }

//*************************************Users*******************************************************************************
    public String getUsersByNameLogin( String username , String password )
    {
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Users u;
        try
        {
            Query q = entity.createNamedQuery( "Users.findByUsername" ).
                setParameter( "username" , username );

            u = ( Users ) q.getSingleResult();
            u.setSaleList( null );
            String cadena = "";
            if ( u.getPassword().equals( password ) )
            {
                cadena = "Usuario y contraseña correcta";
                m.setCode( 200 );
            }
            else
            {
                cadena = "Contraseña incorrecta";
                m.setCode( 406 );
            }

            m.setMsg( cadena );
            m.setDetails( "OK" );
        }
        catch ( IllegalArgumentException e )
        {
            m.setCode( 406 );
            m.setMsg( "Error Ilegal de tipo de dato" );
            m.setDetails( "Advertencia" );
        }
        catch ( NoResultException e )
        {
            m.setCode( 406 );
            m.setMsg( "Usuario no Registrado" );
            m.setDetails( "Advertencia" );
        }
        return gson.toJson( m );

    }

}
