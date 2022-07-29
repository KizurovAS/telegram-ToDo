require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        script:
            $client.docs = $client.docs || [];
            $client.bells = $client.bells || [];
        
        a: Привет, я помогу тебе сохранить заметки и напомнить о важных событиях.
        a: Введите текст для создания заметки.
        go!: /Home
        
    state: Home
        # a: 
        script:
            if ($client.docs.length==0)
                $client.docs.push("0");
             if ($client.bells.length==0)
                $client.bells.push("0");
        buttons:
            "Настройки" -> /Home/Setup
            "Справка" -> /Home/Help
            "Информация" -> /Home/Info
            "Заметки" -> /Home/Docs
            "Напоминания" -> /Home/Bell

        state: Setup
            a: язык интерфейса - русский
            a: шрифт - стандартный
            a: оформление - текстовые кнопки
            buttons:
                "Домой" -> /Home
                "Справка"
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
            a: Раздел еще не готов
            go!: /Home
        
        state: Info
            script: 
                $temp.docs=$client.docs.length-1;
                $temp.bells=$client.bells.length-1;
                $temp.answer="Заметок: "+$temp.docs+". \n Напоминаний: "+$temp.bells+".";
            a: {{$temp.answer}}
            go!: /Home
        
        state: Docs
            
            buttons:
                "Домой" -> /Home
                "Справка" -> /Home/Help
                "Информация" -> /Home/Info
                "Показать все" -> /Home/Docs/SendAllDocs
                "Удалить все" -> /Home/Docs/DeleteAllDocs
                "Добавить" -> /Home/Docs/AddDoc
            
            state: SendAllDocs
                # script:
                #     $temp.i=$temp.i||[]
                #     if (!$temp.i.length)
                #         $temp.i=0;
                # if: $temp.i<=$temp.docs
                #     script:
                #         $temp.i+=1;
                #     a: {{client.dogs[$temp.i]}}
                # a: Заметка {{$client.docs[1]}}
                # a: Заметка {{$client.docs[2]}}
                # a: Итог {{$client.docs[3]}}    
            
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
                    a: все заметки удалены.
                    go!: /Home
                    
                state:
                    q: (нет/отмена/неверно/не верно/не надо/ненадо)
                    go!: /Home/Docs
            
            state: AddDoc
                if: $request.query=="Добавить"
                    a: Введите текст заметки.
                else:
                    script:
                        $client.docs.push($request.query);
                    a: Добавил в заметку: {{$request.query}}
                go!: /Home
                
        state: Bell
            a: Раздел еще не готов
            go!: /Home
            
    # state: Hello
    #     intent!: /привет
    #     a: Привет привет

    # state: Bye
    #     intent!: /пока
    #     a: Пока пока

    state: NoMatch
        event!: noMatch
        go!: /Home/Docs/AddDoc

    state: Match
        event!: match
        a: {{$context.intent.answer}}