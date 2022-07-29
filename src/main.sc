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
            a: Раздел еще не готов
            go!: /Home
        
        state: Docs
            a: Надо сделать вывод всех данных
            script:
                # var data=true;
                $response.data = $response.data || [];
                $response.data.push("1");
                $response.data.push("2");
                $response.data.push("3");
                
            a: Заметка {{$response.data[0]}}
            a: Заметка {{$temp.data}}
            a: Итог {{$response.out}}
            buttons:
                "Домой" -> /Home
                "Справка" -> /Home/Help
                "Информация" -> /Home/Info
                "Показать все" 
                "Добавить"
            
        
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
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}