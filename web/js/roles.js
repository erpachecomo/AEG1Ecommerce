//Este metodo se ejecuta cuando carga toda la pagina
    $( function ()
    {

        /*var tel=document.getElementById('tel');
         tel.setCustomValidity("El formato debe ser +99");
         */


        $( '#frmRole' ).validate( {
            rules: {
                rolename: {
                    minlength: 3 ,
                    maxlength: 20 ,
                    required: true
                }
            } ,
            messages: {
                rolename: {
                    minlength: "Introduzca al menos 3 caracteres" ,
                    maxlength: "Introduzca maximo 20 caracteres" ,
                    required: "Capture el nombre del rol"
                }
            } ,
            highlight: function ( element )
            {
                $( element ).closest( '.form-group' ).addClass( 'has-error' );
            } ,
            unhighlight: function ( element )
            {
                $( element ).closest( '.form-group' ).removeClass( 'has-error' );
            } ,
            errorElement: 'span' ,
            errorClass: 'help-block' ,
            errorPlacement: function ( error , element )
            {
                if ( element.parent( '.input-group' ).length )
                {
                    error.insertAfter( element.parent() );
                }
                else
                {
                    error.insertAfter( element );
                }
            } ,
            submitHandler: function ( form )
            {
                newRole();
                return false;
            }

        } );
        $( '#tbRoles' ).DataTable( {
            languaje: {
                url: "//cdn.datatables.net/plug-ins/1.10.10/i18n/Spanish.json"
            } ,
            ajax: {
                url: "GetRoles" ,
                dataSrc: function ( json )
                {
                    //console.log("Resultado:"+json['msg']);
                    return $.parseJSON( json['msg'] );
                }
            } ,
            columns: [
                {
                    //data:"roleid"
                    data: function ( row )
                    {
                        str = " <div align='right'>";
                        str += accounting.formatMoney( row["roleid"] );
                        str += "</div>";
                        return str;
                    }
                } ,
                {
                    data: "rolename"
                } ,
                {
                    data: function ( row )
                    {
                        //console.log(row);
                        str = " <div align='center'>"
                        str += "<button id='btnBorrar' class='btn btn-danger btn-xs' onClick='deleteRole(" + row['roleid'] + ")'><i class='glyphicon glyphicon-trash'></i> Eliminar </button>";
                        str += "&nbsp;<button id='btnEditar' class = 'btn btn-success btn-xs' onClick = 'showRole(" + row['roleid'] + ",\"" + row['rolename'] + "\")'><i class='glyphicon glyphicon-edit'></i> Modificar </button>";
                        str += "</div>"
                        return str;

                    }

                } ]
        } );
        $( '#frmEditRole' ).validate( {
            rules: {
                rolename2: {
                    minlength: 3 ,
                    maxlength: 20 ,
                    required: true
                }
            } ,
            messages: {
                rolename2: {
                    minlength: "Introduzca al menos 3 caracteres" ,
                    maxlength: "Introduzca maximo 20 caracteres" ,
                    required: "Capture el nombre del rol"
                }
            } ,
            highlight: function ( element )
            {
                $( element ).closest( '.form-group' ).addClass( 'has-error' );
            } ,
            unhighlight: function ( element )
            {
                $( element ).closest( '.form-group' ).removeClass( 'has-error' );
            } ,
            errorElement: 'span' ,
            errorClass: 'help-block' ,
            errorPlacement: function ( error , element )
            {
                if ( element.parent( '.input-group' ).length )
                {
                    error.insertAfter( element.parent() );
                }
                else
                {
                    error.insertAfter( element );
                }
            } ,
            submitHandler: function ( form )
            {
                updateRole();
                return false;
            }

        } );
        $( '#btnModificar' ).on( 'click' , function ()
        {
            $( '#frmEditRole' ).submit();
        } );
        $.ajax( {
            url: 'GetRoles' ,
            type: 'GET' ,
            dataType: 'json'
        } ).done( function ( json )
        {
            if ( json.code === 200 )
                $.each( $.parseJSON( json.msg ) , function ( i , row )
                {
                    $( '<option></option>' , { text: row.rolename } ).attr( 'value' , row.roleid ).appendTo( '#cbRoles' );
                } );
        } );
    } );

    function newRole()
    {
        $.ajax( {
            url: "NewRole" ,
            type: "post" ,
            /*Manda todo el formulario
             * como mandar parametros por separado en data:
             */
            data: $( '#frmRole' ).serialize()
        } ).done(
          function ( data )
          {
              // alert("Se realizo correctamente"+data.code);
              if ( data.code === 200 )
              {

                  $.growl.notice( { message: data.msg + " " + data.details } );
                  $( '#tbRoles' ).dataTable().api().ajax.reload();
                  $( '#rolename' ).val( "" );
              }
              else
                  $.growl.error( { message: data.msg + "" + data.details } );
          }
        ).fail(
          function ()
          {
              $.growl.error( { message: "Algo va mal no se encuentra el servidor" } )
          }
        );
    }

    function deleteRole( roleid )
    {

        swal(
          {
            title: "¿Estas seguro que deseas eliminar este registro?" , text: "accounting plugin 42123123: " + accounting.formatMoney( 42123123 ) ,
              type: "warning" , showCancelButton: true ,
              confirmButtonColor: "#DD6B55" , confirmButtonText: "Aceptar!" ,
              cancelButtonText: "Cancelar" , closeOnConfirm: false ,
              closeOnCancel: false
          } , function ( isConfirm )
        {
            if ( isConfirm )
            {

                var para = {
                    "roleid": roleid
                };
                ///Comienza a Borrar
                $.ajax( {
                    url: "DeleteRol" ,
                    type: "post" ,
                    /*Manda todo el formulario
                     * como mandar parametros por separado en data:
                     */
                    data: para

                } ).done(
                  function ( data )
                  {
                      // alert("Se realizo correctamente"+data.code);
                      if ( data.code === 200 )
                      {
                          $.growl.notice( { message: data.msg + " " + data.details } );
                          swal( "Eliminado!" , "El registro se elimino correctamente" , "success" );
                          $( '#tbRoles' ).dataTable().api().ajax.reload();
                          $( '#rolename' ).val( "" );
                      }
                      else
                          $.growl.error( { message: data.msg + "" + data.details } );
                  }
                ).fail(
                  function ()
                  {
                      $.growl.error( { message: "Algo va mal no se encuentra el servidor" } )
                  }
                );
            }
            else
            {
                swal( "Cancelado" , "Accion Cancelada" , "error" );
            }
        } );

    }

    function showRole( roleid , rolename )
    {
        $( '#roleid' ).val( roleid );
        $( '#rolename2' ).val( rolename );
        $( '#modalRole' ).modal( "show" );
    }


    function updateRole()
    {
        swal(
          {
              title: "¿Estas seguro que deseas actualizar este registro?" , text: "" ,
              type: "warning" , showCancelButton: true ,
              confirmButtonColor: "#DD6B55" , confirmButtonText: "Aceptar!" ,
              cancelButtonText: "Cancelar" , closeOnConfirm: false ,
              closeOnCancel: false
          } , function ( isConfirm )
        {
            if ( isConfirm )
            {

                var para = {
                    "roleid": $( '#roleid' ).val() ,
                    "rolename": $( '#rolename2' ).val()

                };
                ///Comienza a Borrar
                $.ajax( {
                    url: "UpdateRole" ,
                    type: "post" ,
                    /*Manda todo el formulario
                     * como mandar parametros por separado en data:
                     */
                    data: para

                } ).done(
                  function ( data )
                  {
                      // alert("Se realizo correctamente"+data.code);
                      if ( data.code === 200 )
                      {
                          $.growl.notice( { message: data.msg + " " + data.details } );
                          swal( "Actualizado!" , "El registro se Actualizo correctamente" , "success" );
                          $( '#tbRoles' ).dataTable().api().ajax.reload();
                          $( '#modalRole' ).modal( "hide" );
                          //$('#rolename').val("");
                      }
                      else
                          $.growl.error( { message: data.msg + "" + data.details } );
                  }
                ).fail(
                  function ()
                  {
                      $.growl.error( { message: "Algo va mal no se encuentra el servidor" } )
                  }
                );
            }
            else
            {
                swal( "Cancelado" , "Accion Cancelada" , "error" );
            }
        } );

    }









