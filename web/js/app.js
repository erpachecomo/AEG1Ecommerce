    $( function ()
    {
        $( '#nav a' ).on( 'click' , function ( e )
        {
            e.preventDefault();
            //guardar los atributos dentro de page
            var page = $( this ).attr( 'href' );
            $( '#content' ).load( page , { redirect: 'no' } );
            $( '#nav li' ).removeClass( 'active' );
            $( this ).parent().addClass( 'active' );
        } );



    } );