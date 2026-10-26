package com.example.data.local

import com.example.data.model.GameItem

object InitialGameData {

    private fun catalogGame(
        id: Long,
        title: String,
        titleAr: String,
        developer: String,
        releaseYear: Int,
        platforms: String,
        genres: String,
        coverUrl: String,
        gradientColors: String,
        synopsis: String,
        communityRating: Float
    ): GameItem = GameItem(
        id = id,
        title = title,
        titleAr = titleAr,
        developer = developer,
        releaseYear = releaseYear,
        platforms = platforms,
        genres = genres,
        coverUrl = coverUrl,
        gradientColors = gradientColors,
        synopsis = synopsis,
        communityRating = communityRating,
        userRating = 0.0f,
        status = "NONE",
        hoursPlayed = 0.0f,
        userReview = "",
        reviewTitle = "",
        pros = "",
        cons = "",
        subGameplayScore = 0.0f,
        subStoryScore = 0.0f,
        subVisualsScore = 0.0f,
        subAudioScore = 0.0f,
        isFavorite = false,
        loggedDate = 0L,
        platformPlayed = ""
    )

    fun getInitialGames(): List<GameItem> {
        return listOf(
            // 1. Elden Ring
            catalogGame(
                id = 1L,
                title = "Elden Ring",
                titleAr = "ألدن رينج",
                developer = "FromSoftware",
                releaseYear = 2022,
                platforms = "PC, PS5, PS4, Xbox Series X, Xbox One",
                genres = "Action RPG, Soulslike, Open World",
                coverUrl = "https://media.rawg.io/media/games/b29/b294fdd866dcdb643e7bab370a552855.jpg",
                gradientColors = "#EAB308,#78350F",
                synopsis = "نهوض الملطخ (Tarnished) في الأراضي الواقعة بينهما ليصبح لورد الخاتم العظيم في عالم أسطوري واسع صممه هيديتاكا ميازاكي وجورج ر.ر. مارتن.",
                communityRating = 4.9f
            ),
            // 2. Red Dead Redemption 2
            catalogGame(
                id = 2L,
                title = "Red Dead Redemption 2",
                titleAr = "ريد ديد ريدمبشن 2",
                developer = "Rockstar Games",
                releaseYear = 2018,
                platforms = "PC, PS4, Xbox One",
                genres = "Open World, Action, Adventure, Western",
                coverUrl = "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg",
                gradientColors = "#EF4444,#7F1D1D",
                synopsis = "رحلة آرثر مورغان وعصابة فان دير ليند في الغرب الأمريكي الموحش وتلاشي عصر الخارجين عن القانون وسط خيانات وتحولات درامية عميقة.",
                communityRating = 4.9f
            ),
            // 3. Baldur's Gate 3
            catalogGame(
                id = 3L,
                title = "Baldur's Gate 3",
                titleAr = "بوابات بالدور 3",
                developer = "Larian Studios",
                releaseYear = 2023,
                platforms = "PC, PS5, Xbox Series X",
                genres = "RPG, Turn-Based, Strategy, Fantasy",
                coverUrl = "https://media.rawg.io/media/games/699/699222161a5d3429b75939b3603099a2.jpg",
                gradientColors = "#3B82F6,#1E3A8A",
                synopsis = "مغامرة لعب أدوار ملحمية في عالم D&D حيث تتصارع مع طفيلية التهام العقول وسط اختيارات أخلاقية معقدة وحرية استراتيجية مطلقة.",
                communityRating = 4.9f
            ),
            // 4. The Witcher 3: Wild Hunt
            catalogGame(
                id = 4L,
                title = "The Witcher 3: Wild Hunt",
                titleAr = "ذا ويتشر 3: الصيد البري",
                developer = "CD Projekt RED",
                releaseYear = 2015,
                platforms = "PC, PS5, PS4, Xbox Series X, Switch",
                genres = "Action RPG, Open World, Story Rich, Dark Fantasy",
                coverUrl = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10f6bcdbc.jpg",
                gradientColors = "#F97316,#7C2D12",
                synopsis = "جيرالت أوف ريفيا، صائد الوحوش المأجور، يبحث عن طفلة النبوءة سيري في عالم خيالي مزقته الحروب والوحوش.",
                communityRating = 4.8f
            ),
            // 5. God of War Ragnarök
            catalogGame(
                id = 5L,
                title = "God of War Ragnarök",
                titleAr = "إله الحرب: راجناروك",
                developer = "Santa Monica Studio",
                releaseYear = 2022,
                platforms = "PS5, PS4, PC",
                genres = "Action, Adventure, Mythology, Hack and Slash",
                coverUrl = "https://media.rawg.io/media/games/9aa/9aa42d16d425fa6f17045512fa4b4169.jpg",
                gradientColors = "#06B6D4,#164E63",
                synopsis = "كريتوس وأتريوس يخوضان رحلة أسطورية عبر العوالم التسعة لمواجهة مصير راجناروك الحتمي وآلهة أسغارد.",
                communityRating = 4.9f
            ),
            // 6. God of War (2018)
            catalogGame(
                id = 6L,
                title = "God of War",
                titleAr = "إله الحرب (2018)",
                developer = "Santa Monica Studio",
                releaseYear = 2018,
                platforms = "PC, PS4, PS5",
                genres = "Action, Adventure, Mythology",
                coverUrl = "https://media.rawg.io/media/games/4be/4be6a6ad0364723a962f08deedd97168.jpg",
                gradientColors = "#64748B,#1E293B",
                synopsis = "بداية ملحمة كريتوس الجديدة في الأراضي الإسكندنافية برفقة ابنه أتريوس لنثر رماد زوجته في أعلى قمة بالعوالم.",
                communityRating = 4.9f
            ),
            // 7. Cyberpunk 2077
            catalogGame(
                id = 7L,
                title = "Cyberpunk 2077",
                titleAr = "سايبربانك 2077",
                developer = "CD Projekt RED",
                releaseYear = 2020,
                platforms = "PC, PS5, Xbox Series X, PS4",
                genres = "Action RPG, Cyberpunk, Open World, Sci-Fi",
                coverUrl = "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c424deaa.jpg",
                gradientColors = "#FACC15,#713F12",
                synopsis = "في مدينة نايت سيتي المستقبلية، يقود المرتزق V معركة للبقاء بعد زرع شريحة عصبية تحمل شخصية جوني سيلفرهاند.",
                communityRating = 4.7f
            ),
            // 8. Grand Theft Auto V
            catalogGame(
                id = 8L,
                title = "Grand Theft Auto V",
                titleAr = "جراند ثفت أوتو 5",
                developer = "Rockstar North",
                releaseYear = 2013,
                platforms = "PC, PS5, PS4, Xbox Series X, Xbox One",
                genres = "Action, Open World, Crime, Multiplayer",
                coverUrl = "https://media.rawg.io/media/games/456/456dea5e1c7e3cd07060c14e96612001.jpg",
                gradientColors = "#10B981,#064E3B",
                synopsis = "ثلاثة مجرمين غير متوقعين يخاطرون بكل شيء في سلسلة من السرقات الجريئة عبر مدينة لوس سانتوس ومقاطعة بلين.",
                communityRating = 4.8f
            ),
            // 9. Black Myth: Wukong
            catalogGame(
                id = 9L,
                title = "Black Myth: Wukong",
                titleAr = "بلاك ميث: ووكونغ",
                developer = "Game Science",
                releaseYear = 2024,
                platforms = "PC, PS5",
                genres = "Action RPG, Soulslike, Mythology, Fantasy",
                coverUrl = "https://media.rawg.io/media/games/a93/a9386d34e9e030a08e16972ebbe53a2b.jpg",
                gradientColors = "#D97706,#78350F",
                synopsis = "رحلة القدر للمقدر (Destined One) مستوحاة من الأسطورة الصينية الكلاسيكية رحلة إلى الغرب بمواجهات ملحمية مع اليوكاي.",
                communityRating = 4.8f
            ),
            // 10. Ghost of Tsushima
            catalogGame(
                id = 10L,
                title = "Ghost of Tsushima",
                titleAr = "شبح تسوشيما",
                developer = "Sucker Punch Productions",
                releaseYear = 2020,
                platforms = "PS5, PS4, PC",
                genres = "Action, Adventure, Samurai, Open World",
                coverUrl = "https://media.rawg.io/media/games/b49/b49eac9f90813f07b225945dad4e3710.jpg",
                gradientColors = "#E11D48,#881337",
                synopsis = "الساموراي جين ساكاي يضحي بكل تقاليد الشرف ليتحول إلى الشبح ويحرر جزيرة تسوشيما من الغزو المغولي الغاشم.",
                communityRating = 4.8f
            ),
            // 11. Hades
            catalogGame(
                id = 11L,
                title = "Hades",
                titleAr = "هاديس",
                developer = "Supergiant Games",
                releaseYear = 2020,
                platforms = "PC, PS5, PS4, Xbox Series X, Switch",
                genres = "Roguelike, Action, Hack and Slash, Mythology",
                coverUrl = "https://media.rawg.io/media/games/1f4/1f4dd0154e0300d788f5501f0643ef97.jpg",
                gradientColors = "#DC2626,#991B1B",
                synopsis = "زاغريوس أمير العالم السفلي يحاول الهروب من مملكة والده هاديس بمساعدة آلهة الأولمب في تجربة روجلايك سريعة ومتجددة.",
                communityRating = 4.9f
            ),
            // 12. Resident Evil 4 Remake
            catalogGame(
                id = 12L,
                title = "Resident Evil 4",
                titleAr = "ريزدنت إيفل 4",
                developer = "Capcom",
                releaseYear = 2023,
                platforms = "PC, PS5, PS4, Xbox Series X",
                genres = "Survival Horror, Action, Third-Person Shooter",
                coverUrl = "https://media.rawg.io/media/games/858/858c016dec0e7953bb8bc3c5f6e103b1.jpg",
                gradientColors = "#991B1B,#450A0A",
                synopsis = "العميل ليون س. كينيدي يتم إرساله إلى قرية إسبانية معزولة وموبوءة لإنقاذ ابنة الرئيس الأمريكي المخطوفة.",
                communityRating = 4.8f
            ),
            // 13. Silent Hill 2 (Remake)
            catalogGame(
                id = 13L,
                title = "Silent Hill 2",
                titleAr = "سايلنت هيل 2",
                developer = "Bloober Team / Konami",
                releaseYear = 2024,
                platforms = "PC, PS5",
                genres = "Psychological Horror, Survival Horror",
                coverUrl = "https://media.rawg.io/media/games/4cf/4cf6b8f1c99859f5187e1f409549f2b8.jpg",
                gradientColors = "#475569,#0F172A",
                synopsis = "جيمس سندرلاند يتلقى رسالة من زوجته المتوفاة ماري تستدعيه إلى بلدة التل الصامت الضبابية الغامضة.",
                communityRating = 4.8f
            ),
            // 14. Hollow Knight
            catalogGame(
                id = 14L,
                title = "Hollow Knight",
                titleAr = "هولو نايت",
                developer = "Team Cherry",
                releaseYear = 2017,
                platforms = "PC, Switch, PS4, Xbox One",
                genres = "Metroidvania, 2D, Difficult, Atmospheric",
                coverUrl = "https://media.rawg.io/media/games/4cf/4cfc03aeb23078cc3a61f10126238da7.jpg",
                gradientColors = "#6366F1,#1E1B4B",
                synopsis = "استكشف مملكة هالونيست القديمة المليئة بالحشرات والغموض والمخاطر في تحفة ميترويدفينيا كلاسيكية ثنائية الأبعاد.",
                communityRating = 4.8f
            ),
            // 15. Marvel's Spider-Man Remastered
            catalogGame(
                id = 15L,
                title = "Marvel's Spider-Man",
                titleAr = "سبايدرمان",
                developer = "Insomniac Games",
                releaseYear = 2018,
                platforms = "PC, PS5, PS4",
                genres = "Action, Superhero, Open World, Adventure",
                coverUrl = "https://media.rawg.io/media/games/9aa/9aa42d16d425fa6f17045512fa4b4169.jpg",
                gradientColors = "#EF4444,#1E3A8A",
                synopsis = "بيتر باركر يتأرجح عبر نيويورك ويوازن بين حياته الشخصية الممزقة وواجبه كبطل خارق في مواجهة أشرار مارفل الكبار.",
                communityRating = 4.7f
            ),
            // 16. The Last of Us Part I
            catalogGame(
                id = 16L,
                title = "The Last of Us Part I",
                titleAr = "ذا لاست أوف أس",
                developer = "Naughty Dog",
                releaseYear = 2022,
                platforms = "PC, PS5",
                genres = "Action, Adventure, Story Rich, Post-Apocalyptic",
                coverUrl = "https://media.rawg.io/media/games/d82/d82364905c23a7b38d08c8d001781a92.jpg",
                gradientColors = "#15803D,#14532D",
                synopsis = "رحلة جول وإيلي عبر الولايات المتحدة المدمرة بوباء الفطريات في قصة إنسانية مؤثرة ومؤلمة تفيض بالمشاعر.",
                communityRating = 4.9f
            ),
            // 17. Grand Theft Auto VI
            catalogGame(
                id = 17L,
                title = "Grand Theft Auto VI",
                titleAr = "جراند ثفت أوتو 6",
                developer = "Rockstar Games",
                releaseYear = 2025,
                platforms = "PS5, Xbox Series X",
                genres = "Action, Open World, Crime, Adventure",
                coverUrl = "https://media.rawg.io/media/games/25c/25c3a7eab5fa5d60326e398d470d70b6.jpg",
                gradientColors = "#EC4899,#831843",
                synopsis = "العودة إلى ولاية ليونيدا وشوارع فايس سيتي المضاءة بالنيون في أضخم قصة جريمة ورومانسية بين لوسيا وجيسون.",
                communityRating = 5.0f
            ),
            // 18. Astro Bot
            catalogGame(
                id = 18L,
                title = "Astro Bot",
                titleAr = "أسترو بوت",
                developer = "Team ASOBI",
                releaseYear = 2024,
                platforms = "PlayStation 5",
                genres = "Platformer, 3D, Family, Adventure",
                coverUrl = "https://media.rawg.io/media/games/83b/83b50c184ec81014a938c53724c3e387.jpg",
                gradientColors = "#38BDF8,#0369A1",
                synopsis = "مغامرة منصات ثلاثية الأبعاد تنبض بالبهجة والابتكار تحتفي بتاريخ بلايستيشن وتستغل كامل قدرات يد DualSense.",
                communityRating = 4.9f
            ),
            // 19. Bloodborne
            catalogGame(
                id = 19L,
                title = "Bloodborne",
                titleAr = "بلودبورن",
                developer = "FromSoftware",
                releaseYear = 2015,
                platforms = "PlayStation 4",
                genres = "Action RPG, Soulslike, Dark Fantasy, Lovecraftian",
                coverUrl = "https://media.rawg.io/media/games/214/2143def5122e23b16954209930f1d0b5.jpg",
                gradientColors = "#334155,#0F172A",
                synopsis = "رحلة الصائد عبر مدينة يارنام القوطية الملعونة لكشف أسرار طاعون الوحوش ورعب الآلهة الكبرى الكوني.",
                communityRating = 4.9f
            ),
            // 20. DOOM Eternal
            catalogGame(
                id = 20L,
                title = "DOOM Eternal",
                titleAr = "دوم إيتيرنال",
                developer = "id Software",
                releaseYear = 2020,
                platforms = "PC, PS5, PS4, Xbox Series X, Switch",
                genres = "FPS, Fast-Paced, Gore, Heavy Metal",
                coverUrl = "https://media.rawg.io/media/games/587/587588c64afbff80e6f444eb2e46f9da.jpg",
                gradientColors = "#DC2626,#7F1D1D",
                synopsis = "سلاير ينطلق لسحق شياطين الجحيم على سطح كوكب المريخ بسرعة فائقة وأسلحة فتاكة وموسيقى ميتال حارقة.",
                communityRating = 4.8f
            ),
            // 21. HELLDIVERS 2
            catalogGame(
                id = 21L,
                title = "Helldivers 2",
                titleAr = "هيل دايفرز 2",
                developer = "Arrowhead Game Studios",
                releaseYear = 2024,
                platforms = "PC, PS5",
                genres = "Third-Person Shooter, Co-op, Sci-Fi",
                coverUrl = "https://media.rawg.io/media/screenshots/755/7552f856f88b0de1dee040121039b079.jpg",
                gradientColors = "#EAB308,#854D0E",
                synopsis = "قاتل من أجل الحرية عبر مجرة معادية في لعبة إطلاق نار جماعية سريعة وتكتيكية تنبض بالإثارة والنيران الصديقة الكوميدية.",
                communityRating = 4.7f
            ),
            // 22. Metaphor: ReFantazio
            catalogGame(
                id = 22L,
                title = "Metaphor: ReFantazio",
                titleAr = "ميتافور: ريفانتازيو",
                developer = "Studio Zero / Atlus",
                releaseYear = 2024,
                platforms = "PC, PS5, PS4, Xbox Series X",
                genres = "JRPG, Turn-Based, Fantasy, Adventure",
                coverUrl = "https://media.rawg.io/media/games/2cd/2cd2467a32aaaed0bdeb192c2831cfe0.jpg",
                gradientColors = "#6366F1,#312E81",
                synopsis = "من صناع بيرسونا، رحلة خيالية سياسية للتنافس على العرش في مملكة أوكروكرونيا عبر عالم مليء بالقوى السحرية والآركتايب.",
                communityRating = 4.9f
            ),
            // 23. Final Fantasy VII Rebirth
            catalogGame(
                id = 23L,
                title = "Final Fantasy VII Rebirth",
                titleAr = "فاينل فانتسي 7: ريبيرث",
                developer = "Square Enix",
                releaseYear = 2024,
                platforms = "PlayStation 5",
                genres = "Action RPG, Fantasy, Open World",
                coverUrl = "https://media.rawg.io/media/games/511/511995d5dfcf18965dbb354d2ba9e176.jpg",
                gradientColors = "#06B6D4,#0E7490",
                synopsis = "كلاود ورفاقه يغادرون مدينة ميدغار لاستكشاف الكوكب الشاسع ومطاردة سيفيروث وسط أسرار مصيرية وذكريات غامضة.",
                communityRating = 4.8f
            ),
            // 24. Assassin's Creed Shadows
            catalogGame(
                id = 24L,
                title = "Assassin's Creed Shadows",
                titleAr = "أساسنز كريد شادوز",
                developer = "Ubisoft",
                releaseYear = 2025,
                platforms = "PC, PS5, Xbox Series X",
                genres = "Action, Stealth, Historical, Open World",
                coverUrl = "https://media.rawg.io/media/games/526/526881e0f5f8c1550e51df3801f96ea3.jpg",
                gradientColors = "#DC2626,#991B1B",
                synopsis = "عش قصتي ناوي الشينوبي السريعة وياسوكي الساموراي الأسطوري في اليابان الإقطاعية في أواخر فترة سينغوكو.",
                communityRating = 4.7f
            )
        )
    }
}
