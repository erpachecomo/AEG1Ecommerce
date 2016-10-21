    var main = function ()
    {
        "use strict";

        $( '#nav a' ).on( 'click' , function ( e )
        {
            e.preventDefault();
            //guardar los atributos dentro de page
            var page = $( this ).attr( 'href' );
            $( '#content' ).load( page , { redirect: 'no' } );
            $( '#nav li' ).removeClass( 'active' );
            $( this ).parent().addClass( 'active' );
        } );

        $( '#pro' ).on( 'click' , function ( e )
        {
            //e.preventDefault();
            swal( "Plugin que muestea 42123123" , accounting.formatMoney( 42123123 ) );
        } );

    }

    $( document ).ready( main );