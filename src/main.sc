require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        script:
            $client.docs = $client.docs || [];
            $client.pos=$client.pos||[];
            # $client.bells = $client.bells || [];
        a: Привет, я помогу тебе сохранить заметки (и напомнить) о важных событиях.
        a: Введите текст для создания заметки.
        go!: /Home
        
    state: Home
        a: Главное меню 
        script:
            if ($client.docs.length==0)
                $client.docs.push("0");
            if ($client.pos.length==0)
                $client.pos.push("/0");
            # if ($client.bells.length==0)
            #     $client.bells.push("0");
        buttons:
            "Настройки" -> /Home/Setup
            "Справка" -> /Home/Help
            "Информация" -> /Home/Info
            "Заметки" -> /Home/Docs
            # "Напоминания" -> /Home/Bell

        state: Setup
            a: язык интерфейса - русский
            a: шрифт - стандартный
            a: оформление - текстовые кнопки
            buttons:
                "Домой" -> /Home
                # "Справка"
                "Язык" -> /Home/Setup/LanguageSetup
                "Шрифт" -> /Home/Setup/FontSetup
                "Оформление" -> /Home/Setup/DecorSetup
            
            state: LanguageSetup
                a: сейчас доступен только Русский язык
                buttons:
                    "Домой" -> /Home
                    "Назад" -> /Home/Setup
                    "RU" -> /Home/Setup
                    "EN" -> /Home/Setup
            
            state: FontSetup
                a: сейчас изменение шрифта недоступно
                buttons: 
                    "Домой" -> /Home
                    "Назад" -> /Home/Setup
                    "Стандарт" -> /Home/Setup
                    "Жирный" -> /Home/Setup
                    "Курсив" -> /Home/Setup
                    "Жирный курсив" -> /Home/Setup
            
            state: DecorSetup
                a: сейчас доступны только текстовые кнопки
                buttons:
                    "Домой" -> /Home
                    "Назад" -> /Home/Setup
                    "Текстовые кнопки" -> /Home/Setup
                    "Значки" -> /Home/Setup
        
        state: Help
            a: Создать заметку можно двумя способами: отправить сообщение с текстом заметки; нажать кнопку добавить, а затем ввести текст заметки.
            a: Изменить ключевые настройки можно в пункте "Настройки" (сейчас настройки изменить невозможно).
            a: Для просмотра заметок нажмите соответствующую кнопку меню.
            a: После вывода заметок по клику на ссылку заметки можно перейти в окно удаления или изменения заметки.
            go!: /Home
        
        state: Info
            script: 
                $temp.docs=$client.docs.length-1;
                # $temp.bells=$client.bells.length-1;
                # $temp.answer="Заметок: "+$temp.docs+". \n Напоминаний: "+$temp.bells+".";
                $temp.answer="Заметок: "+$temp.docs+".";
            a: {{$temp.answer}}
            go!: /Home
        
        state: Docs
            go!: SendAllDocs
            # a: Меню заметок
            # buttons:
            #     "Домой" -> /Home
            #     "Справка" -> /Home/Help
            #     "Информация" -> /Home/Info
            #     "Показать все" -> /Home/Docs/SendAllDocs
            #     "Удалить все" -> /Home/Docs/DeleteAllDocs
            #     "Добавить" -> /Home/Docs/AddDoc
            
            state: SendAllDocs
                a: Список заметок:
                script:
                    $response.replies = $response.replies || [];
                    $temp.i=$temp.i||[]
                    if (!$temp.i.length)
                       $temp.i=0;
                    while ($temp.i < $client.docs.length-1)
                        {$temp.i+=1;
                        $response.replies.push( {
                        type: "text",
                        text: $client.pos[$temp.i]+" "+$client.docs[$temp.i],
                            } )
                        }
                    
                if: $temp.i==0
                    a: Нет заметок.
                    go!: /Home/Docs/
                else:
                    a: Выберете заметку.
                    buttons:
                        "Домой" -> /Home
                        "Справка" -> /Home/Help
                        "Информация" -> /Home/Info
                        # "Показать все" -> /Home/Docs/SendAllDocs
                        "Удалить все" -> /Home/Docs/DeleteAllDocs
                        "Добавить" -> /Home/Docs/AddDoc
            
                state: EditDoc
                    q: *
                    if: $request.query[0]!="/"
                        go!: /Home/Docs/AddDoc
                    script:
                        $response.replies = $response.replies || [];
                        $temp.i=0;
                        $session.j=0;
                        while ($temp.i < $client.docs.length-1)
                            {$temp.i+=1;
                            if ($request.query==$client.pos[$temp.i]) {
                                $session.j=$temp.i;
                                }
                            }            
                    if: $session.j!=0
                        a: {{$client.docs[$session.j]}}
                    else:
                        a: заметка не найдена
                        go!: /Home
                    buttons:
                        "Домой" -> /Home
                        "Показать все" -> /Home/Docs/SendAllDocs
                        "Изменить эту заметку" -> /Home/Docs/SendAllDocs/EditDoc/EditThisDoc
                        "Удалить эту заметку" -> /Home/Docs/DeleteThisDoc
                    
                    state: EditThisDoc
                        if: $request.query=="Изменить эту заметку"
                            a: Введите новый текст заметки
                            script:
                                $session.flug="EditDoc";
        
            state: DeleteThisDoc
                a: Вы действительно хотите удалить эту заметку?
                buttons:
                    "Да"
                    "Нет"
                state:
                    q: (нет/отмена/неверно/не верно/не надо/ненадо)
                    go!: /Home
                state:
                    q: (да/верно/удалить)
                    script:
                        var docs=[];
                        var pos=[];
                        var esc=$session.j;
                        var vol=$client.docs.length
                        $temp.k=0;
                        while ($temp.k<vol) 
                            {
                            if ($temp.k!=esc) {
                                docs.push($client.docs[$temp.k]);
                                pos.push("/"+(docs.length-1));
                                }
                            $temp.k+=1;
                            }
                        delete $client.docs;
                        $client.docs=docs;
                        delete $client.pos;
                        $client.pos=pos;
                    a: Заметка удалена.
                    go!: /Home
            
                    
            state: DeleteAllDocs
                a: Удалить все заметки?
                buttons:
                    "Да"
                    "Нет"
                state:
                    q: (да/верно/удалить)
                    script:
                        delete $client.docs;
                        $client.docs=$client.docs||[];
                        delete $client.pos;
                        $client.pos=$client.pos||[];
                    a: все заметки удалены.
                    go!: /Home
                    
                state:
                    q: (нет/отмена/неверно/не верно/не надо/ненадо)
                    go!: /Home/Docs
            
            state: AddDoc
                if: $request.query=="Добавить"
                    a: Введите текст заметки.
                elseif: $session.flug!="EditDoc"
                    script:
                        $client.docs.push($request.query);
                        $client.pos.push("/"+($client.docs.length-1));
                    a: Добавил в заметку: {{$request.query}}
                    go!: /Home
                elseif: $session.flug=="EditDoc"
                    script:
                        delete $session.flug;
                        $session.buf=$request.query;
                    a: Сохранить новую заметку: {{$request.query}}
                    buttons:
                        "Отмена" -> /Home/Docs/AddDoc/Cansel
                        "Сохранить" -> /Home/Docs/AddDoc/Save
                    
                state: Cansel
                    q: Отмена
                    a: Изменения отменены
                    go!: /Home
                    
                state: Save
                    q: Сохранить
                    a: Изменения сохранены
                    script:
                        $client.docs[$session.j]=$session.buf;
                    go!: /Home
                
        # state: Bell
        #     a: Раздел еще не готов
        #     go!: /Home
            
    state: NoMatch
        event!: noMatch
        go!: /Home/Docs/AddDoc

    state: Match
        event!: match
        a: {{$context.intent.answer}}