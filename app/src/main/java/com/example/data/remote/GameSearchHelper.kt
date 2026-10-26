package com.example.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object GameSearchHelper {

    private val ARABIC_FRANCHISE_MAP = mapOf(
        "فيفا" to "FIFA",
        "فيفا 24" to "EA Sports FC 24",
        "فيفا 23" to "FIFA 23",
        "اف سي" to "EA Sports FC",
        "افسي" to "EA Sports FC",
        "بيس" to "eFootball PES",
        "اي فوتبول" to "eFootball PES",
        "فوتبول" to "eFootball",
        "كود" to "Call of Duty",
        "كول اوف ديوتي" to "Call of Duty",
        "كول اف ديوتي" to "Call of Duty",
        "وارزون" to "Call of Duty Warzone",
        "ماين كرافت" to "Minecraft",
        "ماينكرافت" to "Minecraft",
        "جاتا" to "Grand Theft Auto",
        "جراند" to "Grand Theft Auto",
        "قراند" to "Grand Theft Auto",
        "حرامي السيارات" to "Grand Theft Auto",
        "فايس سيتي" to "Grand Theft Auto Vice City",
        "سان اندرياس" to "Grand Theft Auto San Andreas",
        "ريد ديد" to "Red Dead Redemption",
        "ريد ديد 2" to "Red Dead Redemption 2",
        "رد دد" to "Red Dead Redemption",
        "سبايدرمان" to "Spider-Man",
        "سبايدر مان" to "Spider-Man",
        "اله الحرب" to "God of War",
        "إله الحرب" to "God of War",
        "جود اوف وار" to "God of War",
        "كريتوس" to "God of War",
        "ذا ويتشر" to "The Witcher",
        "ويتشر" to "The Witcher",
        "الدن رينج" to "Elden Ring",
        "ألدن رينج" to "Elden Ring",
        "ايلدن رينج" to "Elden Ring",
        "اساسنز كريد" to "Assassin's Creed",
        "اساسن" to "Assassin's Creed",
        "عقيدة القتلة" to "Assassin's Creed",
        "شادوز" to "Assassin's Creed Shadows",
        "دارك سولز" to "Dark Souls",
        "سولز" to "Dark Souls",
        "بلودبورن" to "Bloodborne",
        "بلود بورن" to "Bloodborne",
        "سيكيرو" to "Sekiro",
        "سايلنت هيل" to "Silent Hill",
        "ريزدنت ايفل" to "Resident Evil",
        "ريزيدنت ايفل" to "Resident Evil",
        "الشر المقيم" to "Resident Evil",
        "انشارتد" to "Uncharted",
        "أنتشارتد" to "Uncharted",
        "باتمان" to "Batman",
        "فارس الظلام" to "Batman",
        "سايبر بانك" to "Cyberpunk 2077",
        "سايبربانك" to "Cyberpunk 2077",
        "هاديس" to "Hades",
        "تومب رايدر" to "Tomb Raider",
        "لارا كروفت" to "Tomb Raider",
        "نيد فور سبيد" to "Need for Speed",
        "كراش" to "Crash Bandicoot",
        "كراش بانديكوت" to "Crash Bandicoot",
        "مورتال كومبات" to "Mortal Kombat",
        "تكن" to "Tekken",
        "تيكن" to "Tekken",
        "ستريت فايتر" to "Street Fighter",
        "فار كراي" to "Far Cry" ,
        "هورايزن" to "Horizon Zero Dawn",
        "هورايزون" to "Horizon Forbidden West",
        "زينوبليد" to "Xenoblade Chronicles",
        "بوكيمون" to "Pokemon",
        "ماريو" to "Super Mario",
        "سوبر ماريو" to "Super Mario",
        "زيلدا" to "The Legend of Zelda",
        "اسطورة زيلدا" to "The Legend of Zelda",
        "سونيك" to "Sonic the Hedgehog",
        "فاينل فانتسي" to "Final Fantasy",
        "فاينال فانتسي" to "Final Fantasy",
        "ستار وورز" to "Star Wars",
        "حرب النجوم" to "Star Wars",
        "ديفل ماي كراي" to "Devil May Cry",
        "ديترويت" to "Detroit: Become Human",
        "فالورانت" to "Valorant",
        "فالورنت" to "Valorant",
        "اوفرواتش" to "Overwatch",
        "ابكس" to "Apex Legends",
        "ابكس ليجندز" to "Apex Legends",
        "فورتنايت" to "Fortnite",
        "فورت نايت" to "Fortnite",
        "ببجي" to "PUBG: Battlegrounds",
        "بوبجي" to "PUBG: Battlegrounds",
        "روكيت ليق" to "Rocket League",
        "روكيت ليج" to "Rocket League",
        "امونج اس" to "Among Us",
        "هيل دايفرز" to "Helldivers 2",
        "هيلدايفرز" to "Helldivers 2",
        "ميتافور" to "Metaphor: ReFantazio",
        "استرو بوت" to "Astro Bot",
        "أسترو بوت" to "Astro Bot",
        "بلاك ميث" to "Black Myth: Wukong",
        "ووكونغ" to "Black Myth: Wukong",
        "ووكونج" to "Black Myth: Wukong",
        "بالدورز جيت" to "Baldur's Gate 3",
        "بوابات بالدور" to "Baldur's Gate 3",
        "هولو نايت" to "Hollow Knight",
        "دووم" to "DOOM",
        "دوم" to "DOOM",
        "هالو" to "Halo",
        "ديابلو" to "Diablo",
        "فول اوت" to "Fallout",
        "فولوت" to "Fallout",
        "سكايرم" to "The Elder Scrolls V: Skyrim",
        "ماس ايفكت" to "Mass Effect",
        "بايوشوك" to "BioShock",
        "ديسهونورد" to "Dishonored",
        "هيتمان" to "Hitman",
        "ميتال جير" to "Metal Gear Solid",
        "ديث ستراندنج" to "Death Stranding",
        "ديد سبيس" to "Dead Space",
        "الان ويك" to "Alan Wake",
        "ألان ويك" to "Alan Wake",
        "كنترول" to "Control",
        "جوست اوف تسوشيما" to "Ghost of Tsushima",
        "شبح تسوشيما" to "Ghost of Tsushima",
        "ياكوزا" to "Yakuza",
        "مونستر هانتر" to "Monster Hunter",
        "بيرسونا" to "Persona",
        "دراجون بول" to "Dragon Ball",
        "ناروتو" to "Naruto",
        "ون بيس" to "One Piece",
        "ستاردو فالي" to "Stardew Valley",
        "تيراريا" to "Terraria",
        "سوبنوتيكا" to "Subnautica",
        "نو مانز سكاي" to "No Man's Sky",
        "سي اوف ثيفز" to "Sea of Thieves",
        "بحر اللصوص" to "Sea of Thieves",
        "رست" to "Rust",
        "ارك" to "ARK: Survival Evolved",
        "بال وورلد" to "Palworld",
        "بالورلد" to "Palworld",
        "ليثل كومباني" to "Lethal Company",
        "سيفيليزيشن" to "Civilization",
        "سيمز" to "The Sims",
        "فورزا" to "Forza Horizon",
        "جران توريزمو" to "Gran Turismo",
        "اسيتو كورسا" to "Assetto Corsa",
        "ان بي ايه" to "NBA 2K",
        "دبليو دبليو اي" to "WWE 2K"
    )

    fun resolveSearchQuery(query: String): String {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return ""

        // Check if query is in Arabic (contains characters in Arabic unicode block)
        val hasArabic = trimmed.any { it in '\u0600'..'\u06FF' }
        if (!hasArabic) {
            return trimmed
        }

        // Clean query: remove diacritics, normalize alif
        val normalized = trimmed
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .lowercase()

        // 1. Direct key match
        for ((arKey, enName) in ARABIC_FRANCHISE_MAP) {
            val keyNorm = arKey
                .replace('أ', 'ا')
                .replace('إ', 'ا')
                .replace('آ', 'ا')
                .replace('ة', 'ه')
                .replace('ى', 'ي')
                .lowercase()

            if (normalized.contains(keyNorm) || keyNorm.contains(normalized)) {
                return enName
            }
        }

        // 2. Simple transliteration fallback
        val sb = StringBuilder()
        for (ch in normalized) {
            when (ch) {
                'ا' -> sb.append("a")
                'ب' -> sb.append("b")
                'ت' -> sb.append("t")
                'ث' -> sb.append("th")
                'ج' -> sb.append("j")
                'ح' -> sb.append("h")
                'خ' -> sb.append("kh")
                'د' -> sb.append("d")
                'ذ' -> sb.append("th")
                'ر' -> sb.append("r")
                'ز' -> sb.append("z")
                'س' -> sb.append("s")
                'ش' -> sb.append("sh")
                'ص' -> sb.append("s")
                'ض' -> sb.append("d")
                'ط' -> sb.append("t")
                'ظ' -> sb.append("z")
                'ع' -> sb.append("a")
                'غ' -> sb.append("gh")
                'ف' -> sb.append("f")
                'ق' -> sb.append("q")
                'ك' -> sb.append("k")
                'ل' -> sb.append("l")
                'م' -> sb.append("m")
                'ن' -> sb.append("n")
                'ه' -> sb.append("h")
                'و' -> sb.append("o")
                'ي' -> sb.append("y")
                ' ' -> sb.append(" ")
                else -> if (ch in 'a'..'z' || ch in '0'..'9') sb.append(ch)
            }
        }
        val transliterated = sb.toString().trim()
        return if (transliterated.isNotBlank()) transliterated else trimmed
    }

    fun optimizePosterUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        // RAWG CDN media URLs support dynamic cropping/resizing:
        // transforms heavy original images to lightweight 600x400 thumbnails for 4x faster loading
        return if (url.contains("media.rawg.io/media/") && !url.contains("/crop/") && !url.contains("/resize/")) {
            url.replace("media.rawg.io/media/", "media.rawg.io/media/crop/600/400/")
        } else {
            url
        }
    }

    fun getFallbackPosterForGame(title: String, genres: String): String {
        val lowerGenres = genres.lowercase()
        val lowerTitle = title.lowercase()
        return when {
            lowerTitle.contains("gta") || lowerTitle.contains("grand theft auto") ->
                "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg"
            lowerTitle.contains("red dead") ->
                "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg"
            lowerTitle.contains("witcher") ->
                "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"
            lowerTitle.contains("elden") || lowerTitle.contains("souls") ->
                "https://media.rawg.io/media/games/b29/b294fdd866dcdb643e7bab370a552855.jpg"
            lowerTitle.contains("cyberpunk") ->
                "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg"
            lowerTitle.contains("spider") ->
                "https://media.rawg.io/media/games/7ae/7ae5a14cdb4ab222a134c15f4629e430.jpg"
            lowerTitle.contains("helldivers") ->
                "https://media.rawg.io/media/screenshots/755/7552f856f88b0de1dee040121039b079.jpg"
            lowerTitle.contains("metaphor") ->
                "https://media.rawg.io/media/games/2cd/2cd2467a32aaaed0bdeb192c2831cfe0.jpg"
            lowerTitle.contains("final fantasy") ->
                "https://media.rawg.io/media/games/511/511995d5dfcf18965dbb354d2ba9e176.jpg"
            lowerTitle.contains("assassin") ->
                "https://media.rawg.io/media/games/526/526881e0f5f8c1550e51df3801f96ea3.jpg"
            lowerGenres.contains("shooter") || lowerGenres.contains("fps") ->
                "https://media.rawg.io/media/games/587/587588c64afbff80e6f444eb2e46f9da.jpg"
            lowerGenres.contains("horror") ->
                "https://media.rawg.io/media/games/09b/09b41c1a2c5761c5b1772a4ae238bb0e.jpg"
            lowerGenres.contains("platformer") || lowerGenres.contains("indie") ->
                "https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg"
            lowerGenres.contains("rpg") ->
                "https://media.rawg.io/media/games/699/69907ecf13f172e9e144069769c3be73.jpg"
            else ->
                "https://media.rawg.io/media/games/b7d/b7d3f1715fa8381a4e780173a197a615.jpg"
        }
    }

    fun launchGameTrailer(context: Context, gameTitle: String) {
        val query = "$gameTitle official trailer 4k"
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "جاري فتح العرض الدعائي لـ $gameTitle", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchGameGameplay(context: Context, gameTitle: String) {
        val query = "$gameTitle gameplay walkthrough 4k"
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "جاري استعراض طريقة اللعب لـ $gameTitle", Toast.LENGTH_SHORT).show()
        }
    }
}
