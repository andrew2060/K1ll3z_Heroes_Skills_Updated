package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Setting;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;

public class SkillLightningBlade extends ActiveSkill
{
  public SkillLightningBlade(Heroes plugin)
  {
    super(plugin, "LightningBlade");
    setDescription("Your sword is imbued with electical energy and your attacks call lightning down!");
    setUsage("/skill lightningblade");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill lightningblade" });
    setTypes(new SkillType[] { SkillType.LIGHTNING, SkillType.HARMFUL, SkillType.BUFF });
    Bukkit.getServer().getPluginManager().registerEvents(new SkillDamageListener(this), plugin);
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.DURATION.node(), Integer.valueOf(30000));
    node.set(Setting.EXPIRE_TEXT.node(), "%hero% no longer has lightningblade!");
    return node;
  }

  public void init()
  {
    super.init();
  }

  public SkillResult use(Hero hero, String[] args)
  {
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
    hero.addEffect(new LightningBladeEffect(this, duration));
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    return getDescription();
  }

  public class LightningBladeEffect extends ExpirableEffect
  {
    public LightningBladeEffect(Skill skill, long duration)
    {
      super("LightningBladeEffect", duration);
      this.types.add(EffectType.BENEFICIAL);
      this.types.add(EffectType.DISPELLABLE);
      this.types.add(EffectType.FIRE);
    }
  }

  public class SkillDamageListener implements Listener
  {
    private final Skill skill;

    public SkillDamageListener(Skill skill)
    {
      this.skill = skill;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
      if ((event.isCancelled()) || (!(event instanceof EntityDamageByEntityEvent)))
        return;
      EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent)event;
      if (!(subEvent.getDamager() instanceof Player))
        return;
      Player player = (Player)subEvent.getDamager();
      Hero hero = SkillLightningBlade.this.plugin.getCharacterManager().getHero(player);
      if (!hero.hasEffect("LightningBladeEffect")) {
        return;
      }
      event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillLightningBlade
 * JD-Core Version:    0.6.2
 */