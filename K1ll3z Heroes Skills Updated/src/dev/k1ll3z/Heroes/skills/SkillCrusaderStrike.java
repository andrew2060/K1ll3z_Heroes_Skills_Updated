package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillCrusaderStrike extends TargettedSkill
{
  public SkillCrusaderStrike(Heroes plugin)
  {
    super(plugin, "CrusaderStrike");
    setDescription("You strike your enemy heavily with you blade dealing $1 light damage!");
    setUsage("/skill crusaderstrike");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill crusaderstrike", "skill cstrike" });
    setTypes(new SkillType[] { SkillType.DAMAGING, SkillType.LIGHT, SkillType.HARMFUL });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(15));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 15, false);
    addSpellTarget(target, hero);
    damageEntity(target, player, damage, DamageCause.MAGIC);
    broadcastExecuteText(hero, target);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 15, false);
    return getDescription().replace("$1", damage + "");
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillCrusaderStrike
 * JD-Core Version:    0.6.2
 */