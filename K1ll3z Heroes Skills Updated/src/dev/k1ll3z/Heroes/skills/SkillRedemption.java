package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

public class SkillRedemption extends TargettedSkill
{
  public SkillRedemption(Heroes plugin)
  {
    super(plugin, "Redemption");
    setDescription("You sacrifice $1 hp to deal $2 damage to your opponent!");
    setUsage("/skill redemption <target>");
    setArgumentRange(0, 1);
    setIdentifiers(new String[] { "skill redemption" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.DAMAGING, SkillType.SILENCABLE });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(10));
    node.set(SkillSetting.HEALTH.node(), Integer.valueOf(5));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    if (!(target instanceof Player)) {
      return SkillResult.INVALID_TARGET;
    }
    Player player = hero.getPlayer();
    int hpMinus = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH, 5, false);
    double hhealth = hero.getEntity().getHealth();

    if (hhealth < 10) {
      Messaging.send(player, "You're health is too low!", new Object[0]);
      return SkillResult.INVALID_TARGET_NO_MSG;
    }

    HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, hpMinus, this);
    this.plugin.getServer().getPluginManager().callEvent(hrhEvent);

    hero.getEntity().setHealth(hhealth - hrhEvent.getAmount());

    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 10, false);
    addSpellTarget(target, hero);
    damageEntity(target, player, damage, DamageCause.MAGIC);

    broadcastExecuteText(hero, target);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 10, false);
    int hpMinus = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH, 15, false);
    return getDescription().replace("$1", hpMinus + "").replace("$2", damage + "");
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillRedemption
 * JD-Core Version:    0.6.2
 */